package kornell.server.api

import javax.ws.rs.Consumes
import javax.ws.rs.PUT
import javax.ws.rs.Path
import kornell.core.event.ActomEntered
import kornell.core.event.EnrollmentStateChanged
import kornell.server.jdbc.SQL._
import kornell.server.jdbc.repository.EventsRepo
import kornell.core.event.AttendanceSheetSigned
import kornell.core.event.CourseClassStateChanged
import kornell.server.jdbc.repository.EventsRepo
import javax.inject.Inject

@Path("events")
class EventsResource @Inject() (
	eventsRepo:EventsRepo
  ){
  
  def this() = this(null)
  
  @PUT
  @Path("actomEntered")
  @Consumes(Array(ActomEntered.TYPE))
  def putActomEntered(event:ActomEntered){
	 eventsRepo.logActomEntered(event)	
  }
  
  @PUT
  @Path("enrollmentStateChanged")
  @Consumes(Array(EnrollmentStateChanged.TYPE))
  def putEnrollmentStateChanged(event:EnrollmentStateChanged){
     eventsRepo.logEnrollmentStateChanged(event)
  }
  
  @PUT
  @Path("courseClassStateChanged")
  @Consumes(Array(CourseClassStateChanged.TYPE))
  def putCourseClassStateChanged(event:CourseClassStateChanged){
     eventsRepo.logCourseClassStateChanged(event)
  }
  
  @PUT
  @Path("attendanceSheetSigned")
  @Consumes(Array(AttendanceSheetSigned.TYPE))
  def putAttendanceSheetSigned(event:AttendanceSheetSigned){
     eventsRepo.logAttendanceSheetSigned(event)
  }
	
}