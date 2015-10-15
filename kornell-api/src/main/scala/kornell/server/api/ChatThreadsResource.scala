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
import kornell.core.util.StringUtils
import kornell.core.entity.ChatThreadType
import kornell.server.util.Conditional.toConditional
import kornell.server.jdbc.repository.PersonRepo

@Path("chatThreads")
@Produces(Array(ChatThread.TYPE))
class ChatThreadsResource {
  
  @POST
  @Path("courseClass/{courseClassUUID}/support")
  @Produces(Array("application/octet-stream"))
  def postMessageToCourseClassSupportThread(implicit @Context sc: SecurityContext, 
    @PathParam("courseClassUUID") courseClassUUID: String,
    message: String) = AuthRepo().withPerson { person => 
  		ChatThreadsRepo.postMessageToCourseClassThread(person.getUUID, courseClassUUID, message, ChatThreadType.SUPPORT)
  		ChatThreadsRepo.getCourseClassChatThreadUUID(person.getUUID, courseClassUUID: String, ChatThreadType.SUPPORT).get
  }
  
  @POST
  @Path("courseClass/{courseClassUUID}/tutoring")
  @Produces(Array("application/octet-stream"))
  def postMessageToCourseClassTutoringThread(implicit @Context sc: SecurityContext, 
    @PathParam("courseClassUUID") courseClassUUID: String,
    message: String) = AuthRepo().withPerson { person => 
        ChatThreadsRepo.postMessageToCourseClassThread(person.getUUID, courseClassUUID, message, ChatThreadType.TUTORING)
  		ChatThreadsRepo.getCourseClassChatThreadUUID(person.getUUID, courseClassUUID: String, ChatThreadType.TUTORING).get
  }
  
  @POST
  @Path("courseClass/{courseClassUUID}/institutionSupport")
  @Produces(Array("application/octet-stream"))
  def postMessageToInstitutionSupportThread(implicit @Context sc: SecurityContext, 
    @PathParam("courseClassUUID") courseClassUUID: String,
    message: String) = AuthRepo().withPerson { person => 
        ChatThreadsRepo.postMessageToCourseClassThread(person.getUUID, courseClassUUID, message, ChatThreadType.INSTITUTION_SUPPORT)
  		ChatThreadsRepo.getCourseClassChatThreadUUID(person.getUUID, courseClassUUID: String, ChatThreadType.INSTITUTION_SUPPORT).get
  }
  
  @POST
  @Path("platformSupport")
  @Produces(Array("application/octet-stream"))
  def postMessageToPlatformSupportThread(implicit @Context sc: SecurityContext,
    message: String) = AuthRepo().withPerson { person => 
        ChatThreadsRepo.postMessageToInstitutionThread(person.getUUID, person.getInstitutionUUID, message, ChatThreadType.PLATFORM_SUPPORT)
  		ChatThreadsRepo.getInstitutionChatThreadUUID(person.getUUID, person.getInstitutionUUID: String, ChatThreadType.PLATFORM_SUPPORT).get
  }
  
  @POST
  @Path("{chatThreadUUID}/message")
  @Produces(Array(ChatThreadMessagesTO.TYPE))
  def postMessageToChatThread(implicit @Context sc: SecurityContext, 
    @PathParam("chatThreadUUID") chatThreadUUID: String,
    message: String, 
    @QueryParam("since") since: String) = AuthRepo().withPerson { person => 
  		ChatThreadsRepo.createChatThreadMessage(chatThreadUUID, person.getUUID, message)
  		if(StringUtils.isSome(since))
  			ChatThreadsRepo.getChatThreadMessagesSince(chatThreadUUID, since)
  		else
  			ChatThreadsRepo.getChatThreadMessages(chatThreadUUID)
  }
  
  @POST
  @Path("direct/{personUUID}")
  @Produces(Array("text/plain"))
  def postMessageToDirectThread(@PathParam("personUUID") targetPersonUUID: String, message: String) = {
        ChatThreadsRepo.postMessageToDirectThread(getAuthenticatedPersonUUID, targetPersonUUID, message)
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
    @PathParam("chatThreadUUID") chatThreadUUID: String, 
    @QueryParam("since") since: String, 
    @QueryParam("before") before: String) = AuthRepo().withPerson { person => {
      ChatThreadsRepo.markAsRead(chatThreadUUID, person.getUUID)
  		if(StringUtils.isSome(since))
  			ChatThreadsRepo.getChatThreadMessagesSince(chatThreadUUID, since)
  		else if(StringUtils.isSome(before))
  			ChatThreadsRepo.getChatThreadMessagesBefore(chatThreadUUID, before)
  		else
  			ChatThreadsRepo.getChatThreadMessages(chatThreadUUID)
    }
  }
}