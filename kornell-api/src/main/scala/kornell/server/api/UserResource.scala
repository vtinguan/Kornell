package kornell.server.api

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.collection.JavaConverters.setAsJavaSetConverter
import scala.collection.immutable.Set
import javax.servlet.http.HttpServletRequest
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
import kornell.core.entity.RegistrationType
import kornell.core.error.exception.EntityNotFoundException
import kornell.core.error.exception.UnauthorizedAccessException
import kornell.core.to.RegistrationRequestTO
import kornell.core.to.UserInfoTO
import kornell.core.util.UUID
import kornell.server.jdbc.SQL.SQLHelper
import kornell.server.jdbc.repository.AuthRepo
import kornell.server.jdbc.repository.EnrollmentsRepo
import kornell.server.jdbc.repository.InstitutionRepo
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
import kornell.server.jdbc.repository.TokenRepo
import kornell.server.util.Conditional.toConditional
import javax.ws.rs.POST
import kornell.server.util.AccessDeniedErr
//TODO Person/People Resource
@Path("user")
class UserResource(private val authRepo:AuthRepo) {
  def this() = this(AuthRepo())
  
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
			    InstitutionsRepo.byName(name)
			  else
			    InstitutionsRepo.byHostName(hostName)
      }.getOrElse(null));
    
    val auth = req.getHeader("X-KNL-TOKEN")
    if (auth != null && auth.length() > 0 && userHello.getInstitution != null) {
      val token = TokenRepo.checkToken(auth)
      if (token.isDefined) {
    	  val person = PersonRepo(token.get.getPersonUUID).first.getOrElse(null)
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
    val username = PersonRepo(person.getUUID).getUsername
    user.setUsername(username)
    user.setPerson(person)
    user.setLastPlaceVisited(person.getLastPlaceVisited)
    val roles = authRepo.rolesOf(person.getUUID)
    user.setRoles((Set.empty ++ roles).asJava)
    user.setEnrollments(newEnrollments(EnrollmentsRepo.byPerson(person.getUUID)))
    if(RegistrationType.username.equals(person.getRegistrationType)){
    	user.setInstitutionRegistrationPrefix(InstitutionRepo(person.getInstitutionUUID).getInstitutionRegistrationPrefixes.getInstitutionRegistrationPrefixes
    		.asScala.filter(irp => irp.getUUID.equals(person.getInstitutionRegistrationPrefixUUID)).head)
    }
    
    Option(user)
  } 
  
  @GET
  @Path("{personUUID}")
  @Produces(Array(UserInfoTO.TYPE))
  def getByPersonUUID(implicit @Context sc: SecurityContext,
    @PathParam("personUUID") personUUID: String): Option[UserInfoTO] =
    authRepo.withPerson { p =>
      val user = newUserInfoTO
      val person = PersonRepo(personUUID).get
      if (person != null) {
        user.setPerson(person)
        user.setUsername(PersonRepo(person.getUUID).getUsername)
	    if(RegistrationType.username.equals(person.getRegistrationType)){
	    	user.setInstitutionRegistrationPrefix(InstitutionRepo(person.getInstitutionUUID).getInstitutionRegistrationPrefixes.getInstitutionRegistrationPrefixes
	    		.asScala.filter(irp => irp.getUUID.equals(person.getInstitutionRegistrationPrefixUUID)).head)
	    }
        Option(user)
      } else {
        throw new EntityNotFoundException("personNotFound")
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
  def requestPasswordChange(@PathParam("email") email: String,
    @PathParam("institutionName") institutionName: String) = {
    val institution = InstitutionsRepo.byName(institutionName)
    val person = PeopleRepo.getByEmail(institution.get.getUUID, email)
    if (person.isDefined && institution.isDefined) {
      val requestPasswordChangeUUID = UUID.random
      authRepo.updateRequestPasswordChangeUUID(person.get.getUUID, requestPasswordChangeUUID)
      EmailService.sendEmailRequestPasswordChange(person.get, institution.get, requestPasswordChangeUUID)
    } else {
      throw new EntityNotFoundException("personOrInstitutionNotFound")
    }
  }
  
  @PUT
  @Path("resetPassword/{passwordChangeUUID}")
  @Produces(Array(UserInfoTO.TYPE))
  def resetPassword(@PathParam("passwordChangeUUID") passwordChangeUUID: String, password: String) = {
    val person = authRepo.getPersonByPasswordChangeUUID(passwordChangeUUID)
    if (person.isDefined) {
      PersonRepo(person.get.getUUID).updatePassword(person.get.getUUID, password)
      val user = newUserInfoTO
      user.setUsername(person.get.getEmail())
      Option(user)
    } else {
      throw new UnauthorizedAccessException("passwordChangeFailed")
    }
  }

  @PUT
  @Path("changePassword/{targetPersonUUID}/")
  @Produces(Array("text/plain"))
  def changePassword(implicit @Context sc: SecurityContext,
    @PathParam("targetPersonUUID") targetPersonUUID: String, password: String) = {
    authRepo.withPerson { p =>
      if (!PersonRepo(p.getUUID).hasPowerOver(targetPersonUUID))
        throw new UnauthorizedAccessException("passwordChangeDenied")
      else {
        val targetPersonRepo = PersonRepo(targetPersonUUID)
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
    @PathParam("targetPersonUUID") targetPersonUUID: String) = {
    authRepo.withPerson { p =>
      PersonRepo(p.getUUID).hasPowerOver(targetPersonUUID)
    }
  }

  @PUT
  @Path("registrationRequest")
  @Consumes(Array(RegistrationRequestTO.TYPE))
  @Produces(Array(UserInfoTO.TYPE))
  def createUser(regReq: RegistrationRequestTO) = RegistrationEnrollmentService.userRequestRegistration(regReq)

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
    userInfo: UserInfoTO,
    @PathParam("personUUID") personUUID: String) = authRepo.withPerson { p =>
    if (userInfo != null) {
      if (!PersonRepo(p.getUUID).hasPowerOver(personUUID))
        throw new UnauthorizedAccessException("passwordChangeDenied")
      else {
	      PersonRepo(personUUID).update(userInfo.getPerson)
	
	      val roles = authRepo.rolesOf(userInfo.getPerson.getUUID)
	      userInfo.setRoles((Set.empty ++ roles).asJava)
	      userInfo.setEnrollments(newEnrollments(EnrollmentsRepo.byPerson(p.getUUID)))
	      userInfo
      }
    }
  }
    
  @PUT
  @Path("acceptTerms")
  @Consumes(Array("text/plain"))
  @Produces(Array(UserInfoTO.TYPE))
  def acceptTerms() = AuthRepo().withPerson{ p =>
    PersonRepo(p.getUUID).acceptTerms
    getUser(p)
  }

  def createUser(institutionUUID: String, fullName: String, email: String, cpf: String, username: String, password: String): String = {
    val regreq = TOs.newRegistrationRequestTO(institutionUUID, fullName, email, password,cpf,username, RegistrationType.email)
    createUser(regreq).getPerson.getUUID
  }
  
  //This is a temporary call that converts user passwords to bcrypt versions and logs everyone out.
  @GET
  @Path("update/updatePasswordsBCrypt")
  @Produces(Array("text/plain"))
  def updatePasswordsToSalted(implicit @Context sc: SecurityContext) = {
      AuthRepo().updatePasswordsToSalted()
      "OK"
  }

}

object UserResource {
  def apply(authRepo:AuthRepo):UserResource = new UserResource(AuthRepo()) 
  def apply():UserResource = apply(AuthRepo())
}
