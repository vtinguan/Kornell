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
import kornell.server.repository.jdbc.Auth
import kornell.server.repository.jdbc.Institutions
import kornell.server.repository.jdbc.PersonRepository
import kornell.server.repository.jdbc.Registrations
import kornell.server.repository.jdbc.Enrollments
import kornell.server.repository.jdbc.SQLInterpolation._
import kornell.server.repository.service.RegistrationEnrollmentService
import kornell.server.util.EmailService

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
    	val roles = Auth.rolesOf(username)
    	user.setRoles((Set.empty ++ roles).asJava)
    	user.setRegistrationsTO(Registrations.getAll(p))
    	user.setEnrollmentsTO(newEnrollmentsTO(Enrollments.byPerson(p.getUUID)))
    	
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
    	
    	if(confirmation.equals(user.getPerson().getConfirmation())){
    		Auth.confirmAccount(user.getPerson().getUUID())
    		user.getPerson().setConfirmation("")
    	}
    	val roles = Auth.rolesOf(user.getUsername)
    	user.setRoles((Set.empty ++ roles).asJava)
    	user.setRegistrationsTO(Registrations.getAll(p))
    	user.setEnrollmentsTO(newEnrollmentsTO(Enrollments.byPerson(p.getUUID)))
    	
    	Option(user)
  }
  
  @GET
  @Path("{personUUID}")
  @Produces(Array(UserInfoTO.TYPE))
  def getByUsername(implicit @Context sc: SecurityContext,
	    @Context resp:HttpServletResponse,
	    @PathParam("personUUID") personUUID:String):Option[UserInfoTO] =
    Auth.withPerson { p =>
    	val user = newUserInfoTO
	    val person = PersonRepository(personUUID).get 
	    if (person.isDefined){
	    	user.setPerson(person.get)
		    user.setUsername(user.getPerson().getEmail())
		    user.setEmail(user.getPerson().getEmail())
	    	user.setRegistrationsTO(Registrations.getAll(person.get))
	    	//val signingNeeded = Registrations.signingNeeded(p)
	    	//user.setSigningNeeded(signingNeeded)
	    	//user.setLastPlaceVisited(p.getLastPlaceVisited)
	    	//val roles = Auth.rolesOf(user.getUsername)
	    	//user.setRoles((Set.empty ++ roles).asJava)
	    	Option(user)
	    }
	    else {
	      resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Person not found.")
	      null
	    }
  }
  
  @GET
  @Path("check/{email}")
  @Produces(Array(UserInfoTO.TYPE))
  def checkUsernameAndEmail(@PathParam("email") email:String):Option[UserInfoTO] = {
  	val user = newUserInfoTO
	//verify if there's a password set for this email
  	//if an admin created this user, there will be an entry on the person table, 
  	//but not one on the Password table
	if(Auth.hasPassword(email))
		user.setEmail(email)
	Option(user)
  }
  
  @GET
  @Path("requestPasswordChange/{email}/{institutionName}")
  @Produces(Array("text/plain"))
  def requestPasswordChange(@Context resp:HttpServletResponse,
      @PathParam("email") email:String, 
      @PathParam("institutionName") institutionName:String) = {
    val person = Auth.getPersonByEmail(email)
    val institution = Institutions.byName(institutionName)
    if(person.isDefined && institution.isDefined){
    	val requestPasswordChangeUUID = UUID.random
    	Auth.updateRequestPasswordChangeUUID(person.get.getUUID, requestPasswordChangeUUID)
    	EmailService.sendEmailRequestPasswordChange(person.get, institution.get, requestPasswordChangeUUID)
    } else {
      resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Person or Institution not found.");
    }
  }

  @GET
  @Path("changePassword/{password}/{passwordChangeUUID}")
  @Produces(Array(UserInfoTO.TYPE))
  def changePassword(@Context resp:HttpServletResponse,
      @PathParam("password") password:String, 
      @PathParam("passwordChangeUUID") passwordChangeUUID:String) = {
	  	val person = Auth.getPersonByPasswordChangeUUID(passwordChangeUUID)
	  	if(person.isDefined){
	  	  PersonRepository(person.get.getUUID).setPassword(person.get.getEmail, password)
		  	val user = newUserInfoTO
			user.setEmail(person.get.getEmail())
			Option(user)
	  	} else {
	  	  resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "It wasn't possible to change your password.")
	  	}
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
  
  @PUT
  @Path("{personUUID}")
  @Consumes(Array(UserInfoTO.TYPE))
  @Produces(Array(UserInfoTO.TYPE))
  def update(implicit @Context sc: SecurityContext, userInfo: UserInfoTO,
	    @PathParam("personUUID") personUUID: String) = Auth.withPerson{ p =>
    PersonRepository(personUUID).update(userInfo.getPerson())
    userInfo
  }
  
  
}
