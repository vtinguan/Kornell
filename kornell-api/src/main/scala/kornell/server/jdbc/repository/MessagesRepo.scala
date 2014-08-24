package kornell.server.jdbc.repository

import java.util.Date
import scala.collection.JavaConverters.asScalaBufferConverter
import scala.collection.JavaConverters.bufferAsJavaListConverter
import kornell.core.entity.Message
import kornell.core.util.UUID
import kornell.server.jdbc.SQL.SQLHelper
import kornell.core.entity.MessagePerson

class MessagesRepo {
}

object MessagesRepo {

  def create(message: Message):Message = {
    if (message.getUUID == null)
      message.setUUID(UUID.random)
    sql""" 
    	insert into Message(uuid, subject, body, senderUUID, parentMessageUUID, sentAt)
    	values(${message.getUUID},
		    ${message.getSubject},
		    ${message.getBody},
		    ${message.getSenderUUID},
		    ${message.getParentMessageUUID},
		    ${message.getSentAt})
    """.executeUpdate
    message
  }
  
  def createMessagePerson(messagePerson: MessagePerson):MessagePerson = {
    if (messagePerson.getUUID == null)
      messagePerson.setUUID(UUID.random)
    sql""" 
    	insert into MessagePerson(uuid, messageUUID, recipientUUID, institutionUUID, readAt, archivedAt, messageType)
    	values(${messagePerson.getUUID},
		    ${messagePerson.getMessageUUID},
		    ${messagePerson.getRecipientUUID},
		    ${messagePerson.getInstitutionUUID},
		    ${messagePerson.getReadAt},
		    ${messagePerson.getArchivedAt},
		    ${messagePerson.getMessageType.toString}) 	
    """.executeUpdate
    messagePerson
  }
  
}