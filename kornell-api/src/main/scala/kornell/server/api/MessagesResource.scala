package kornell.server.api

import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import kornell.core.entity.Message
import kornell.server.jdbc.repository.MessagesRepo

@Path("messages")
@Produces(Array(Message.TYPE))
class MessagesResource {
  
  @POST
  @Consumes(Array(Message.TYPE))
  @Produces(Array(Message.TYPE))
  def create(message: Message) = {
    MessagesRepo.create(message)
  }
}