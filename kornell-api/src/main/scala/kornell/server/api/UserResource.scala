package kornell.server.api

import java.util.UUID
import javax.ws.rs._
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.core.entity.Person
import kornell.core.event.EnrollmentStateChanged
import kornell.core.to.RegistrationRequestTO
import kornell.core.to.UserInfoTO
import kornell.server.repository.TOs
import kornell.server.repository.TOs._
import kornell.server.repository.jdbc.Auth
import kornell.server.repository.jdbc.Institutions
import kornell.server.repository.jdbc.People
import kornell.server.repository.jdbc.PersonRepository
import kornell.server.repository.jdbc.RegistrationRepository
import kornell.server.repository.jdbc.Registrations
import kornell.server.repository.jdbc.SQLInterpolation._
import kornell.server.util.EmailSender
import kornell.core.to.TOFactory
import kornell.server.repository.jdbc.Events
import java.util.Date
import kornell.core.event.EnrollmentStateChanged
import kornell.server.repository.jdbc.EnrollmentRepository
import kornell.core.entity.EnrollmentState
import kornell.server.repository.jdbc.Courses
import kornell.core.to.CourseTO
import scala.collection.SortedSet
import scala.collection.immutable.Set
import scala.collection.JavaConverters._
import kornell.server.repository.jdbc.Enrollments
import kornell.server.repository.jdbc.CourseClasses
import kornell.core.entity.CourseClass
import kornell.server.repository.service.RegistrationEnrollmentService

@Path("user")
class UserResource{

  @GET
  @Produces(Array(UserInfoTO.TYPE))
  //TODO: Cache
  def get(implicit @Context sc: SecurityContext):Option[UserInfoTO] =
    Auth.withPerson { p =>
    	val user = newUserInfoTO
    	val username =  sc.getUserPrincipal().getName()
    	user.setUsername(username)
    	user.setPerson(p)
	    user.setEmail(user.getPerson().getEmail())
    	val signingNeeded = Registrations.signingNeeded(p)
    	user.setSigningNeeded(signingNeeded)
    	user.setLastPlaceVisited(p.getLastPlaceVisited)
    	val institution = Institutions.usersInstitution(p)
    	if(institution.isDefined)
    		user.setInstitutionAssetsURL(institution.get.getTerms)
    	val roles = Auth.rolesOf(username)
    	user.setRoles((Set.empty ++ roles).asJava)
    	Option(user)
  }

  @GET
  @Path("login/{confirmation}")
  @Produces(Array(UserInfoTO.TYPE))
  def login(implicit @Context sc: SecurityContext,
	    @PathParam("confirmation") confirmation:String):Option[UserInfoTO] =
    Auth.withPerson { p =>
    	val user = newUserInfoTO
    	user.setUsername(sc.getUserPrincipal().getName())
    	user.setPerson(p)
	    user.setEmail(user.getPerson().getEmail())
    	val signingNeeded = Registrations.signingNeeded(p)
    	user.setSigningNeeded(signingNeeded)
    	user.setLastPlaceVisited(p.getLastPlaceVisited)
    	val institution = Institutions.usersInstitution(p)
    	user.setInstitutionAssetsURL(institution.get.getTerms)
    	
    	if(user.getPerson().getConfirmation().equals(confirmation)){
    		Auth.confirmAccount(user.getPerson().getUUID())
    		user.getPerson().setConfirmation("")
    	}
    	
    	Option(user)
  }
  
  @GET
  @Path("{username}")
  @Produces(Array(UserInfoTO.TYPE))
  def getByUsername(implicit @Context sc: SecurityContext,
	    @PathParam("username") username:String):Option[UserInfoTO] =
    Auth.withPerson { p =>
    	val user = newUserInfoTO
	    val person: Option[Person] = Auth.getPerson(username)    
	    if (person.isDefined)
	    	user.setPerson(person.get)
	    else throw new IllegalArgumentException(s"User [$username] not found.")
	    user.setUsername(username)
	    user.setEmail(user.getPerson().getEmail())
    	val signingNeeded = Registrations.signingNeeded(p)
    	user.setSigningNeeded(signingNeeded)
    	user.setLastPlaceVisited(p.getLastPlaceVisited)
    	val institution = Institutions.usersInstitution(p)
    	user.setInstitutionAssetsURL(institution.get.getTerms)
    	Option(user)
  }
  
  @GET
  @Path("check/{email}")
  @Produces(Array(UserInfoTO.TYPE))
  def checkUsernameAndEmail(@PathParam("email") email:String):Option[UserInfoTO] = {
      	val user = newUserInfoTO
    	val emailFetched = Auth.getEmail(email)
    	if(emailFetched.isDefined)
    		user.setEmail(emailFetched.get)
    	Option(user)
  }
  
  
  @PUT
  @Path("registrationRequest")
  @Consumes(Array(RegistrationRequestTO.TYPE))
  @Produces(Array(UserInfoTO.TYPE))
  def createUser(regReq:RegistrationRequestTO) = RegistrationEnrollmentService.userRequestRegistration(regReq)
  
  
  @PUT
  @Path("placeChange")
  @Produces(Array("text/plain"))
  def putPlaceChange(implicit @Context sc: SecurityContext, newPlace:String) = 
    Auth.withPerson { p => 
    	sql"""
    	update Person set lastPlaceVisited=$newPlace
    	where uuid=${p.getUUID}
    	""".executeUpdate
    }
}
