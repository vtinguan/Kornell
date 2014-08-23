package kornell.server.api

import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import kornell.core.entity.Message
import kornell.server.jdbc.repository.MessagesRepo
import javax.ws.rs.QueryParam
import kornell.server.repository.Entities
import kornell.core.util.UUID
import java.util.Date
import kornell.server.jdbc.repository.CourseClassRepo
import kornell.core.entity.RoleCategory
import kornell.server.jdbc.repository.RolesRepo
import kornell.server.jdbc.repository.AuthRepo
import javax.ws.rs.core.SecurityContext
import javax.ws.rs.core.Context

@Path("messages")
@Produces(Array(Message.TYPE))
class MessagesResource {
  
  @POST
  @Consumes(Array(Message.TYPE))
  def create(implicit @Context sc: SecurityContext, message: Message,
    @QueryParam("courseClassUUID") courseClassUUID: String,
    @QueryParam("institutionUUID") institutionUUID: String) = 
    AuthRepo().withPerson { person =>
	    if(institutionUUID != null){
		    val messagePerson = Entities.newMessagePerson(UUID.random, new Date(), institutionUUID, null, null, null)
		    if(courseClassUUID != null){
		      val person = RolesRepo.getCourseClassSupportResponsible(courseClassUUID)
		      if(person != null){
		      	messagePerson.setRecipientUUID(person.getUUID)
		      }
		    }
		    if(messagePerson.getRecipientUUID == null){
		      val person = RolesRepo.getInstitutionSupportResponsible(courseClassUUID)
		      if(person != null){
		      	messagePerson.setRecipientUUID(person.getUUID)
		      }
		    }
		    if(messagePerson.getRecipientUUID != null){
		    	val messageCreated = MessagesRepo.create(message)
		    	messagePerson.setMessageUUID(messageCreated.getUUID)
		    	MessagesRepo.createMessagePerson(messagePerson)
		    }
	    }
  }
}