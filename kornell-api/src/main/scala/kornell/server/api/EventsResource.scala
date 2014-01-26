package kornell.server.api

import javax.ws.rs.Consumes
import javax.ws.rs.PUT
import javax.ws.rs.Path
import kornell.core.event.ActomEntered
import kornell.core.event.EnrollmentStateChanged
import kornell.server.jdbc.SQL._
import kornell.server.jdbc.repository.EventsRepo

@Path("events")
class EventsResource {
  
  @PUT
  @Path("actomEntered")
  @Consumes(Array(ActomEntered.TYPE))
  def putActomEntered(event:ActomEntered){
	 EventsRepo.logActomEntered(event)	
  }
  
  @PUT
  @Path("enrollmentStateChanged")
  @Consumes(Array(EnrollmentStateChanged.TYPE))
  def putEnrollmentStateChanged(event:EnrollmentStateChanged){
     EventsRepo.logEnrollmentStateChanged(event)
  }
	
}