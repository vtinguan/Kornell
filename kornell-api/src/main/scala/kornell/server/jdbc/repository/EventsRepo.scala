package kornell.server.jdbc.repository

import java.util.Date
import com.google.web.bindery.autobean.vm.AutoBeanFactorySource
import kornell.core.entity.EnrollmentState
import kornell.core.event.ActomEntered
import kornell.core.event.AttendanceSheetSigned
import kornell.core.event.EnrollmentStateChanged
import kornell.core.event.EventFactory
import kornell.server.jdbc.SQL.SQLHelper
import kornell.server.util.EmailService
import java.text.SimpleDateFormat
import kornell.server.util.Settings
import kornell.server.cep.EnrollmentCEP


object EventsRepo {
  val events = AutoBeanFactorySource.create(classOf[EventFactory])
  
  def newEnrollmentStateChanged = events.newEnrollmentStateChanged.as
  
  def logActomEntered(event: ActomEntered) = {
    sql"""
    insert into ActomEntered(uuid,eventFiredAt,enrollmentUUID,actomKey) 
    values(${event.getUUID},
  		   ${event.getEventFiredAt},
           ${event.getEnrollmentUUID},
		   ${event.getActomKey}); 
	""".executeUpdate
	
	EnrollmentCEP.onProgress(event.getEnrollmentUUID)
  }

  
  def logAttendanceSheetSigned(event: AttendanceSheetSigned) = { 
	val todayStart = new SimpleDateFormat("yyyy-MM-dd  00:00:00").format(event.getEventFiredAt())
	val todayEnd = new SimpleDateFormat("yyyy-MM-dd 23:59:59").format(event.getEventFiredAt())
    // don't log more than once a day
	val attendanceSheetSignedUUID = sql"""
	  select uuid from AttendanceSheetSigned
	  where personUUID=${event.getPersonUUID()}
	  and institutionUUID=${event.getInstitutionUUID()}
	  and eventFiredAt between ${todayStart} and ${todayEnd}"""
    .first
    //println("---------attendanceSheetSignedUUID----------- "+attendanceSheetSignedUUID)
    if(!attendanceSheetSignedUUID.isDefined)
		sql"""
	    insert into AttendanceSheetSigned(uuid,eventFiredAt,institutionUUID,personUUID)
	    values(${event.getUUID},
	  		   ${event.getEventFiredAt},
	           ${event.getInstitutionUUID},
			   ${event.getPersonUUID});
		""".executeUpdate
	  
  }
	
  def logEnrollmentStateChanged(uuid: String, eventFiredAt: Date, fromPersonUUID: String, 
      enrollmentUUID: String, fromState: EnrollmentState, toState: EnrollmentState) = {
	  
	  sql"""insert into EnrollmentStateChanged(uuid,eventFiredAt,person_uuid,enrollment_uuid,fromState,toState)
	    values(${uuid},
			   ${eventFiredAt},
         ${fromPersonUUID},
         ${enrollmentUUID},
         ${fromState.toString},
			   ${toState.toString});
		""".executeUpdate
		
	  sql"""update Enrollment set state = ${toState.toString} where uuid = ${enrollmentUUID};
		""".executeUpdate
		
	  if(EnrollmentState.preEnrolled.equals(toState) || EnrollmentState.enrolled.equals(toState)){
	    val enrollment = EnrollmentRepo(enrollmentUUID).get
	    val x = 
	    if(enrollment.getPerson.getEmail != null && !"true".equals(Settings.get("TEST_MODE").orNull)){
		    val courseClass = CourseClassesRepo(enrollment.getCourseClassUUID).get
		    val course = CoursesRepo.byCourseClassUUID(courseClass.getUUID).get
		    val institution = InstitutionsRepo.byUUID(courseClass.getInstitutionUUID).get
		    EmailService.sendEmailEnrolled(enrollment.getPerson, institution, course)
	    }
	  }
  }
	
  def logEnrollmentStateChanged(event: EnrollmentStateChanged):Unit = 
    logEnrollmentStateChanged(event.getUUID,event.getEventFiredAt,event.getFromPersonUUID,
        event.getEnrollmentUUID,event.getFromState,event.getToState)
	
}