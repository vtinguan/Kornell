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

@Path("chatThreads")
@Produces(Array(ChatThread.TYPE))
class ChatThreadsResource {
  
  @POST
  @Consumes(Array(ChatThread.TYPE))
  @Path("courseClass/{courseClassUUID}")
  def create(implicit @Context sc: SecurityContext, 
    @PathParam("courseClassUUID") courseClassUUID: String,
    message: String) = AuthRepo().withPerson { person => 
  		ChatThreadsRepo.postMessageToCourseClassSupportThread(person.getUUID, courseClassUUID, message)
  }
}