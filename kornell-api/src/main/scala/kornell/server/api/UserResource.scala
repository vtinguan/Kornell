package kornell.server.api

import scala.collection.JavaConverters.setAsJavaSetConverter
import scala.collection.immutable.Set
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.core.entity.Person
import kornell.core.to.RegistrationRequestTO
import kornell.core.to.UserInfoTO
import kornell.core.util.UUID
import kornell.server.jdbc.SQL.SQLHelper
import kornell.server.jdbc.repository.AuthRepo
import kornell.server.jdbc.repository.EnrollmentsRepo
import kornell.server.jdbc.repository.InstitutionsRepo
import kornell.server.jdbc.repository.PeopleRepo
import kornell.server.jdbc.repository.PersonRepo
import kornell.server.repository.Entities.newEnrollments
import kornell.server.repository.TOs
import kornell.server.repository.TOs.newUserHelloTO
import kornell.server.repository.TOs.newUserInfoTO
import kornell.server.repository.service.RegistrationEnrollmentService
import kornell.server.util.EmailService
import kornell.server.web.BasicAuthFilter
import kornell.core.to.UserHelloTO
import kornell.server.repository.service.RegistrationEnrollmentService
import javax.inject.Inject
import kornell.server.cdi.Preferred
import kornell.server.jdbc.repository.PeopleRepo

@Path("user")
class UserResource @Inject() (
    val authRepo:AuthRepo,
    val registrationEnrollmentService:RegistrationEnrollmentService,
    val peopleRepo:PeopleRepo,
    val enrollmentsRepo:EnrollmentsRepo,
    val ittsRepo:InstitutionsRepo) {
  
  def this() = this(null,null,null,null,null)
  
  def get = first.get
     
  
  @Path("hello")
  @Produces(Array(UserHelloTO.TYPE))
  @GET
  def hello(@Context req: HttpServletRequest,
      @QueryParam("name") name:String, 
      @QueryParam("hostName") hostName:String) = {
    val userHello = newUserHelloTO
    
    userHello.setInstitution(
      {
        if(name != null)
			    ittsRepo.byName(name)
			  else
			    ittsRepo.byHostName(hostName)
      }.getOrElse(null));
    
    val auth = req.getHeader("X-KNL-A")
    if (auth != null && auth.length() > 0) {
	    val (username, password, institutionUUID) = BasicAuthFilter.extractCredentials(auth)
	    authRepo.authenticate(userHello.getInstitution.getUUID, username, password).map { personUUID =>
	  		val person = peopleRepo.byUUID(personUUID).first.getOrElse(null)
	  		userHello.setUserInfoTO(getUser(person).getOrElse(null))
	  	}
    }
    
    userHello
  }
  
  @GET
  @Produces(Array(UserInfoTO.TYPE)) //TODO: Cache
  def first: Option[UserInfoTO] =
    authRepo.withPerson { p =>
      getUser(p)
    }

  def getUser(person: Person) = {
    val user = newUserInfoTO
    val username = peopleRepo.byUUID(person.getUUID).getUsername
    user.setUsername(username)
    user.setPerson(person)
    user.setLastPlaceVisited(person.getLastPlaceVisited)
    val roles = authRepo.rolesOf(person.getUUID)
    user.setRoles((Set.empty ++ roles).asJava)
    user.setEnrollments(newEnrollments(enrollmentsRepo.byPerson(person.getUUID)))

    Option(user)
  } 
  
  @GET
  @Path("{personUUID}")
  @Produces(Array(UserInfoTO.TYPE))
  def getByPersonUUID(implicit @Context sc: SecurityContext,
    @Context resp: HttpServletResponse,
    @PathParam("personUUID") personUUID: String): Option[UserInfoTO] =
    authRepo.withPerson { p =>
      val user = newUserInfoTO
      val person = peopleRepo.byUUID(personUUID).get
      if (person != null) {
        user.setPerson(person)
        user.setUsername(peopleRepo.byUUID(personUUID).getUsername)
        Option(user)
      } else {
        resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Person not found.")
        null
      }
    }

  @GET
  @Path("check/{institutionUUID}/{username}")
  @Produces(Array(UserInfoTO.TYPE))
  def checkUsernameAndEmail(@PathParam("username") username: String,
      @PathParam("institutionUUID") institutionUUID: String): Option[UserInfoTO] = {
    val user = newUserInfoTO
    //verify if there's a password set for this email
    if (authRepo.hasPassword(institutionUUID, username))
      user.setUsername(username)
    Option(user)
  }

  @GET
  @Path("requestPasswordChange/{email}/{institutionName}")
  @Produces(Array("text/plain"))
  def requestPasswordChange(@Context resp: HttpServletResponse,
    @PathParam("email") email: String,
    @PathParam("institutionName") institutionName: String) = {
    val institution = ittsRepo.byName(institutionName)
    val person = peopleRepo.getByEmail(institution.get.getUUID, email)
    if (person.isDefined && institution.isDefined) {
      val requestPasswordChangeUUID = UUID.random
      authRepo.updateRequestPasswordChangeUUID(person.get.getUUID, requestPasswordChangeUUID)
      EmailService.sendEmailRequestPasswordChange(person.get, institution.get, requestPasswordChangeUUID)
    } else {
      resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Person or Institution not found.");
    }
  }

  @GET
  @Path("changePassword/{password}/{passwordChangeUUID}")
  @Produces(Array(UserInfoTO.TYPE))
  def changePassword(@Context resp: HttpServletResponse,
    @PathParam("password") password: String,
    @PathParam("passwordChangeUUID") passwordChangeUUID: String) = {
    val person = authRepo.getPersonByPasswordChangeUUID(passwordChangeUUID)
    if (person.isDefined) {
      peopleRepo.byUUID(person.get.getUUID).setPassword(person.get.getInstitutionUUID, person.get.getEmail, password)
      val user = newUserInfoTO
      user.setUsername(person.get.getEmail())
      Option(user)
    } else {
      resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "It wasn't possible to change your password.")
    }
  }

  @PUT
  @Path("changePassword/{targetPersonUUID}/")
  @Produces(Array("text/plain"))
  def changePassword(implicit @Context sc: SecurityContext,
    @Context resp: HttpServletResponse,
    @PathParam("targetPersonUUID") targetPersonUUID: String,
    @QueryParam("password") password: String) = {
    authRepo.withPerson { p =>
      if (!peopleRepo.byUUID(p.getUUID).hasPowerOver(targetPersonUUID))
        resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized attempt to change the password.");
      else {
        val targetPersonRepo = peopleRepo.byUUID(targetPersonUUID)
        val username = authRepo.getUsernameByPersonUUID(targetPersonUUID)

        targetPersonRepo.setPassword(targetPersonRepo.get.getInstitutionUUID,
          if (username.isDefined) {
            username.get
          } else {
            val targetPerson = targetPersonRepo.get
            if (targetPerson.getEmail() != null)
              targetPerson.getEmail()
            else
              targetPerson.getCPF()
          }, password)

      }
    }
  }

  @GET
  @Path("hasPowerOver/{targetPersonUUID}")
  @Produces(Array("application/boolean"))
  def changePassword(implicit @Context sc: SecurityContext,
    @Context resp: HttpServletResponse,
    @PathParam("targetPersonUUID") targetPersonUUID: String) = {
    authRepo.withPerson { p =>
      peopleRepo.byUUID(p.getUUID).hasPowerOver(targetPersonUUID)
    }
  }

  @PUT
  @Path("registrationRequest")
  @Consumes(Array(RegistrationRequestTO.TYPE))
  @Produces(Array(UserInfoTO.TYPE))
  def createUser(regReq: RegistrationRequestTO) = registrationEnrollmentService.userRequestRegistration(regReq)

  @PUT
  @Path("placeChange")
  @Produces(Array("text/plain"))
  def putPlaceChange(implicit @Context sc: SecurityContext, newPlace: String) =
    authRepo.withPerson { p =>
      sql"""
    	update Person set lastPlaceVisited=$newPlace
    	where uuid=${p.getUUID}
    	""".executeUpdate
    }

  @PUT
  @Path("{personUUID}")
  @Consumes(Array(UserInfoTO.TYPE))
  @Produces(Array(UserInfoTO.TYPE))
  def update(implicit @Context sc: SecurityContext, 
    @Context resp: HttpServletResponse,
    userInfo: UserInfoTO,
    @PathParam("personUUID") personUUID: String) = authRepo.withPerson { p =>
    if (userInfo != null) {
      if (!peopleRepo.byUUID(p.getUUID).hasPowerOver(personUUID))
        resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized attempt to change the password.");
      else {
	      peopleRepo.byUUID(personUUID).update(userInfo.getPerson)
	
	      val roles = authRepo.rolesOf(userInfo.getPerson.getUUID)
	      userInfo.setRoles((Set.empty ++ roles).asJava)
	      userInfo.setEnrollments(newEnrollments(enrollmentsRepo.byPerson(p.getUUID)))
	      userInfo
      }
    }
  }
    
  @PUT
  @Path("acceptTerms")
  @Consumes(Array("text/plain"))
  @Produces(Array(UserInfoTO.TYPE))
  def acceptTerms() = authRepo.withPerson{ p =>
    peopleRepo.byUUID(p.getUUID).acceptTerms
    getUser(p)
  }

  def createUser(institutionUUID: String, fullName: String, email: String, cpf: String, username: String, password: String): String = {
    val regreq = TOs.newRegistrationRequestTO(institutionUUID, fullName, email, password,cpf,username)
    createUser(regreq).getPerson.getUUID
  }

}

