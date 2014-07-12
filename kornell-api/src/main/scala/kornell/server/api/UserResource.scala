package kornell.server.api

import scala.collection.JavaConverters._
import scala.collection.immutable.Set
import javax.servlet.http.HttpServletResponse
import javax.ws.rs._
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.core.entity.Person
import kornell.core.to.RegistrationRequestTO
import kornell.core.to.UserInfoTO
import kornell.core.util.UUID
import kornell.server.repository.TOs
import kornell.server.repository.TOs._
import kornell.server.repository.service.RegistrationEnrollmentService
import kornell.server.util.EmailService
import kornell.server.jdbc.repository.AuthRepo
import kornell.server.jdbc.repository.RegistrationsRepo
import kornell.core.entity.Registrations
import kornell.server.jdbc.repository.InstitutionsRepo
import kornell.core.entity.Enrollments
import kornell.server.jdbc.repository.EnrollmentsRepo
import kornell.server.jdbc.SQL._
import kornell.server.jdbc.repository.PersonRepo
import kornell.server.jdbc.repository.PersonRepo
import kornell.server.jdbc.repository.PersonRepo
import kornell.server.jdbc.repository.PeopleRepo
import kornell.core.entity.RoleCategory
import kornell.server.jdbc.repository.CourseClassesRepo
import kornell.core.entity.Role
import kornell.core.entity.RoleType
//TOOD Person/People Resource
@Path("user")
class UserResource(private val authRepo:AuthRepo) {
  def this() = this(AuthRepo())
  
  def get = first.get
  
  @GET
  @Produces(Array(UserInfoTO.TYPE)) //TODO: Cache
  def first: Option[UserInfoTO] =
    authRepo.withPerson { p =>
      val user = newUserInfoTO
      val username = PersonRepo(p.getUUID).getUsername
      user.setUsername(username)
      user.setPerson(p)
      val signingNeeded = RegistrationsRepo.signingNeeded(p)
      user.setSigningNeeded(signingNeeded)
      user.setLastPlaceVisited(p.getLastPlaceVisited)
      val roles = authRepo.rolesOf(username)
      user.setRoles((Set.empty ++ roles).asJava)
      user.setRegistrationsTO(RegistrationsRepo.getAll(p.getUUID))
      user.setEnrollmentsTO(newEnrollmentsTO(EnrollmentsRepo.byPerson(p.getUUID)))

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
      val person = PersonRepo(personUUID).get
      if (person != null) {
        user.setPerson(person)
        if (user.getPerson().getEmail() != null)
          user.setUsername(user.getPerson().getEmail())
        else
          user.setUsername(user.getPerson().getCPF())
        user.setRegistrationsTO(RegistrationsRepo.getAll(person.getUUID))
        //val signingNeeded = RegistrationsRepo.signingNeeded(p)
        //user.setSigningNeeded(signingNeeded)
        //user.setLastPlaceVisited(p.getLastPlaceVisited)
        //val roles = Auth.rolesOf(user.getUsername)
        //user.setRoles((Set.empty ++ roles).asJava)
        Option(user)
      } else {
        resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Person not found.")
        null
      }
    }

  @GET
  @Path("check/{username}")
  @Produces(Array(UserInfoTO.TYPE))
  def checkUsernameAndEmail(@PathParam("username") username: String): Option[UserInfoTO] = {
    val user = newUserInfoTO
    //verify if there's a password set for this email
    if (authRepo.hasPassword(username))
      user.setUsername(username)
    Option(user)
  }

  @GET
  @Path("requestPasswordChange/{email}/{institutionName}")
  @Produces(Array("text/plain"))
  def requestPasswordChange(@Context resp: HttpServletResponse,
    @PathParam("email") email: String,
    @PathParam("institutionName") institutionName: String) = {
    val person = PeopleRepo.getByUsername(email)
    val institution = InstitutionsRepo.byName(institutionName)
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
      PersonRepo(person.get.getUUID).setPassword(person.get.getEmail, password)
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
      if (!PersonRepo(p.getUUID).hasPowerOver(targetPersonUUID))
        resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized attempt to change the password.");
      else {
        val targetPersonRepo = PersonRepo(targetPersonUUID)
        val username = authRepo.getUsernameByPersonUUID(targetPersonUUID)

        targetPersonRepo.setPassword(
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
  def update(implicit @Context sc: SecurityContext, userInfo: UserInfoTO,
    @PathParam("personUUID") personUUID: String) = authRepo.withPerson { p =>
    if (userInfo != null) {
      PersonRepo(personUUID).update(userInfo.getPerson())

      val roles = authRepo.rolesOf(userInfo.getUsername)
      userInfo.setRoles((Set.empty ++ roles).asJava)
      userInfo.setRegistrationsTO(RegistrationsRepo.getAll(p.getUUID))
      userInfo.setEnrollmentsTO(newEnrollmentsTO(EnrollmentsRepo.byPerson(p.getUUID)))
      userInfo
    }
  }

  def createUser(institutionUUID: String, fullName: String, email: String, cpf: String, username: String, password: String): String = {
    val regreq = TOs.newRegistrationRequestTO(institutionUUID, fullName, email, password,cpf,username)
    createUser(regreq).getPerson.getUUID
  }

}

object UserResource {
  def apply(authRepo:AuthRepo):UserResource = new UserResource(AuthRepo()) 
  def apply():UserResource = apply(AuthRepo())
}
