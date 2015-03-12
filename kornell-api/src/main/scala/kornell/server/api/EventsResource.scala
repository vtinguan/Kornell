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
import kornell.core.event.EnrollmentTransfered
import kornell.server.util.Conditional.toConditional
import kornell.server.auth.Authorizator
import kornell.server.util.AccessDeniedErr
import kornell.server.jdbc.repository.PersonRepo
import kornell.server.jdbc.repository.EnrollmentRepo

@Path("events")
class EventsResource @Inject() (
    val auth:Authorizator,
	val eventsRepo:EventsRepo,
	val personRepo: PersonRepo,
	val enrollmentRepo: EnrollmentRepo
  ){
  
  def this() = this(null, null, null, null)
  
  @PUT
  @Path("actomEntered")
  @Consumes(Array(ActomEntered.TYPE))
  def putActomEntered(event:ActomEntered){
	 eventsRepo.logActomEntered(event)	
  }
  
  @PUT
  @Path("enrollmentStateChanged")
  @Consumes(Array(EnrollmentStateChanged.TYPE))
  def putEnrollmentStateChanged(event:EnrollmentStateChanged) = {
     eventsRepo.logEnrollmentStateChanged(event)
  }.requiring(auth.isPlatformAdmin, AccessDeniedErr())
   .or(auth.isInstitutionAdmin(personRepo.withUUID(auth.getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
   .or(auth.isCourseClassAdmin(enrollmentRepo.get(event.getEnrollmentUUID).getCourseClassUUID), AccessDeniedErr())
   .get
  
  @PUT
  @Path("courseClassStateChanged")
  @Consumes(Array(CourseClassStateChanged.TYPE))
  def putCourseClassStateChanged(event:CourseClassStateChanged) = {
     eventsRepo.logCourseClassStateChanged(event)
  }.requiring(auth.isPlatformAdmin, AccessDeniedErr())
   .or(auth.isInstitutionAdmin(personRepo.withUUID(auth.getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
   .or(auth.isCourseClassAdmin(event.getCourseClassUUID), AccessDeniedErr())
   .get
  
  @PUT
  @Path("attendanceSheetSigned")
  @Consumes(Array(AttendanceSheetSigned.TYPE))
  def putAttendanceSheetSigned(event:AttendanceSheetSigned){
     eventsRepo.logAttendanceSheetSigned(event)
  }
	
  @PUT
  @Path("enrollmentTransfered")
  @Consumes(Array(EnrollmentTransfered.TYPE))
  def putEnrollmentTransfered(event:EnrollmentTransfered) = {
     eventsRepo.logEnrollmentTransfered(event)
  }.requiring(auth.isPlatformAdmin, AccessDeniedErr())
   .or(auth.isInstitutionAdmin(personRepo.withUUID(auth.getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
   .or((auth.isCourseClassAdmin(event.getFromCourseClassUUID) && auth.isCourseClassAdmin(event.getToCourseClassUUID)), AccessDeniedErr())
   .get
}