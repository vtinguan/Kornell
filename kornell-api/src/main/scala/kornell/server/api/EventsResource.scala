package kornell.server.api
import javax.ws.rs.Path
import javax.ws.rs.PUT
import javax.ws.rs.Consumes
import kornell.core.event.ActomEntered
import kornell.core.event.Event
import kornell.server.repository.jdbc.Events
import kornell.core.event.EnrollmentStateChanged

@Path("events")
class EventsResource {
  
  @PUT
  @Path("actomEntered")
  @Consumes(Array(ActomEntered.TYPE))
  def putActomEntered(event:ActomEntered){
	 Events.logActomEntered(event)	
  }
  
  @PUT
  @Path("registrationStateChanged")
  @Consumes(Array(EnrollmentStateChanged.TYPE))
  def putRegistrationStateChanged(event:EnrollmentStateChanged){
	 Events.logEnrollmentStateChanged(event)	
  }
	
}