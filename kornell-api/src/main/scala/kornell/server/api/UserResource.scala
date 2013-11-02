package kornell.server.api

import javax.ws.rs._
import javax.ws.rs.core.MediaType._
import scala.reflect.BeanProperty
import javax.ws.rs.core.Response._
import javax.ws.rs.core.Response.Status._
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import javax.persistence.EntityManager
import javax.inject.Inject
import javax.enterprise.context.RequestScoped
import kornell.server.repository.TOs
import kornell.server.repository.jdbc.Auth
import kornell.server.repository.jdbc.Registrations
import kornell.core.to.UserInfoTO
import kornell.server.repository.jdbc.SQLInterpolation._
import kornell.server.repository.jdbc.Institutions
import kornell.server.repository.TOs._
import kornell.core.entity.Person
import kornell.server.repository.jdbc.People
import kornell.server.util.EmailSender
import javax.servlet.http.HttpServletRequest
import java.util.UUID
import kornell.server.repository.jdbc.PersonRepository

@Path("user")
class UserResource{

  @GET
  @Produces(Array(UserInfoTO.TYPE))
  def get(implicit @Context sc: SecurityContext):Option[UserInfoTO] =
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
  @Path("check/{username}/{email}")
  @Produces(Array(UserInfoTO.TYPE))
  def checkUsernameAndEmail(@PathParam("username") username:String,
	    @PathParam("email") email:String):Option[UserInfoTO] = {
      	val user = newUserInfoTO
	    val person: Option[Person] = Auth.getPerson(username)    
	    if (person.isDefined){
	    	user.setPerson(person.get) 
	    	user.setUsername(username)
	    	user.setEmail("")
	    	user.getPerson().setEmail("")
	    	user.getPerson().setSex("")
	    	user.getPerson().setBirthDate(null)
	    	user.getPerson().setLastPlaceVisited("")
	    }
    	val emailFetched = Auth.getEmail(email)
    	if(emailFetched.isDefined)
    		user.setEmail(emailFetched.get)
    	Option(user)
  }
  
  @PUT
  @Path("create")
  @Produces(Array(UserInfoTO.TYPE))
  def createUser(data: String) = {
    val confirmation = UUID.randomUUID.toString
    val aData = data.split("###")
	val username = aData(0)
	val password = aData(1) 
	val email = aData(2) 
	val firstName = aData(3) 
	val lastName = aData(4) 
	val company = aData(5) 
	val title = aData(6)
	val sex = aData(7)
	val birthDate = aData(8)
	val confirmationLink = aData(9) + "#vitrine:" + confirmation
    val institution_uuid = "00a4966d-5442-4a44-9490-ef36f133a259";
    val course_uuid = "d9aaa03a-f225-48b9-8cc9-15495606ac46";
    val p: PersonRepository = People().createPerson(email, firstName, lastName, company, title, sex, birthDate, confirmation);
    p.setPassword(username, password) 
    	.registerOn(institution_uuid)
		.enrollOn(course_uuid, p.get().get.getUUID())
  	val user = newUserInfoTO
    val person: Option[Person] = Auth.getPerson(username)    
    if (person.isDefined){
    	user.setPerson(person.get) 
    }
    user.setUsername(username)
    
    EmailSender.sendEmail(Auth.getPerson(username).get, Institutions.byUUID(institution_uuid).get, confirmationLink)
    
	Option(user)
  }
    
  
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
