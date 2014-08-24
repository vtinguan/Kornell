package kornell.server.api

import java.util.Date

import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.core.entity.Message
import kornell.core.entity.MessageType
import kornell.core.util.UUID
import kornell.server.jdbc.repository.AuthRepo
import kornell.server.jdbc.repository.MessagesRepo
import kornell.server.jdbc.repository.RolesRepo
import kornell.server.repository.Entities

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
		    val messagePerson = Entities.newMessagePerson(UUID.random, new Date(), institutionUUID, null, null, null, null)
		    if(courseClassUUID != null){
		      	messagePerson.setRecipientUUID(courseClassUUID)
		      	messagePerson.setMessageType(MessageType.courseClass)
		    } else {
		      	messagePerson.setRecipientUUID(institutionUUID)
		      	messagePerson.setMessageType(MessageType.institution)
		    }
	    	val messageCreated = MessagesRepo.create(message)
	    	messagePerson.setMessageUUID(messageCreated.getUUID)
	    	MessagesRepo.createMessagePerson(messagePerson)
	    }
  }
}