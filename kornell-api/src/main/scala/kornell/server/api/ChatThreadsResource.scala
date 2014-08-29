package kornell.server.api

import kornell.core.entity.ChatThread
import kornell.core.util.UUID
import kornell.server.jdbc.repository.AuthRepo
import kornell.server.repository.Entities
import javax.ws.rs.Produces
import javax.ws.rs.core.SecurityContext
import javax.ws.rs.Path
import javax.ws.rs.core.Context
import javax.ws.rs.QueryParam
import javax.ws.rs.POST
import javax.ws.rs.Consumes
import javax.ws.rs.PathParam
import kornell.server.jdbc.repository.CourseClassRepo
import kornell.server.jdbc.repository.ChatThreadsRepo
import javax.ws.rs.GET
import kornell.core.to.UnreadChatThreadsTO
import kornell.core.to.ChatThreadMessagesTO

@Path("chatThreads")
@Produces(Array(ChatThread.TYPE))
class ChatThreadsResource {
  
  @POST
  @Path("courseClass/{courseClassUUID}")
  @Produces(Array("text/plain"))
  def postMessageToCourseClassSupportThread(implicit @Context sc: SecurityContext, 
    @PathParam("courseClassUUID") courseClassUUID: String,
    message: String) = AuthRepo().withPerson { person => 
  		ChatThreadsRepo.postMessageToCourseClassSupportThread(person.getUUID, courseClassUUID, message)
  }
  
  @POST
  @Path("{chatThreadUUID}/message")
  @Produces(Array("text/plain"))
  def postMessageToChatThread(implicit @Context sc: SecurityContext, 
    @PathParam("chatThreadUUID") chatThreadUUID: String,
    message: String) = AuthRepo().withPerson { person => 
  		ChatThreadsRepo.createChatThreadMessage(chatThreadUUID, person.getUUID, message)
  }
  
  @Path("unreadCount")
  @Produces(Array("application/octet-stream"))
  @GET
  def getTotalUnreadCountByPerson(implicit @Context sc: SecurityContext, 
    @QueryParam("institutionUUID") institutionUUID: String) = AuthRepo().withPerson { person => 
  		ChatThreadsRepo.getTotalUnreadCountByPerson(person.getUUID, institutionUUID)
  }
  
  @Path("unreadCountPerThread")
  @Produces(Array(UnreadChatThreadsTO.TYPE))
  @GET
  def getTotalUnreadCountsByPersonPerThread(implicit @Context sc: SecurityContext, 
    @QueryParam("institutionUUID") institutionUUID: String) = AuthRepo().withPerson { person => 
  		ChatThreadsRepo.getTotalUnreadCountsByPersonPerThread(person.getUUID, institutionUUID)
  }
  
  @Path("{chatThreadUUID}/messages")
  @Produces(Array(ChatThreadMessagesTO.TYPE))
  @GET
  def getChatThreadMessages(implicit @Context sc: SecurityContext, 
    @PathParam("chatThreadUUID") chatThreadUUID: String) = AuthRepo().withPerson { person => 
  		ChatThreadsRepo.getChatThreadMessages(chatThreadUUID)
  }
}