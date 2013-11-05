package kornell.server.api
import javax.ws.rs.Path
import javax.ws.rs.PUT
import javax.ws.rs.Consumes
import kornell.core.event.ActomEntered
import kornell.server.repository.jdbc.Events

@Path("events")
class EventsResource {
  
  @PUT
  @Path("actomEntered")
  @Consumes(Array(ActomEntered.TYPE))
  //TODO: @Asynchronous
  def putActomEntered(event:ActomEntered):Unit =
    Events.logActomEntered(event)	
}