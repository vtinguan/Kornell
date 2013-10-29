package kornell.server.api
import javax.ws.rs.Path
import javax.ws.rs.PUT
import javax.ws.rs.Consumes
import kornell.core.shared.event.ActomEntered

@Path("events")
class EventsResource {
  
  @PUT
  @Path("actomEntered")
  @Consumes(Array(ActomEntered.TYPE))
  def putActomEntered(event:ActomEntered){
    println(s"Actom [${event.getActomKey()}] entered by [${event.getFromPersonUUID()}]");
  }
	
}