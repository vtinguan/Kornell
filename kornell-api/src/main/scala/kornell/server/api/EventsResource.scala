package kornell.server.api

import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import kornell.server.jdbc.SQL._
import kornell.server.jdbc.repository.AuthRepo
import kornell.server.jdbc.repository.EventsRepo
import kornell.server.jdbc.repository.PersonRepo
import kornell.server.authentication.ThreadLocalAuthenticator
import kornell.core.event.ActomEntered
import kornell.core.event.AttendanceSheetSigned
import kornell.core.event.CourseClassStateChanged
import kornell.core.event.EnrollmentStateChanged
import kornell.core.event.EnrollmentTransferred
import kornell.core.event.EntityChanged
import kornell.core.entity.AuditedEntityType
import kornell.core.to.EntityChangedEventsTO
//TODO: Why are these returning Unit?
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
  
  @PUT
  @Path("courseClassStateChanged")
  @Consumes(Array(CourseClassStateChanged.TYPE))
  def putCourseClassStateChanged(event:CourseClassStateChanged){
     EventsRepo.logCourseClassStateChanged(event)
  }
  
  @PUT
  @Path("attendanceSheetSigned")
  @Consumes(Array(AttendanceSheetSigned.TYPE))
  def putAttendanceSheetSigned(event:AttendanceSheetSigned){
     EventsRepo.logAttendanceSheetSigned(event)
  }
  
  @PUT
  @Path("enrollmentTransferred")
  @Consumes(Array(EnrollmentTransferred.TYPE))
  def putEnrollmentTransferred(event:EnrollmentTransferred) = {
     EventsRepo.logEnrollmentTransferred(event)
  }
  
  @GET
  @Produces(Array(EntityChangedEventsTO.TYPE))
  @Path("entityChanged")
  def getEntityChangedEvents(@QueryParam("entityType") entityType: String, @QueryParam("ps") pageSize: Int, @QueryParam("pn") pageNumber: Int) = 
  AuthRepo().withPerson { person =>
     EventsRepo.getEntityChangedEvents(person.getInstitutionUUID, AuditedEntityType.valueOf(entityType), pageSize, pageNumber)
  }
	
}