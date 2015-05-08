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
import kornell.server.ep.EnrollmentSEP
import kornell.server.util.ServerTime
import kornell.core.event.CourseClassStateChanged
import kornell.core.entity.CourseClassState

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

    EnrollmentSEP.onProgress(event.getEnrollmentUUID)
    EnrollmentSEP.onAssessment(event.getEnrollmentUUID)
  }

  def logAttendanceSheetSigned(event: AttendanceSheetSigned) = {
    val todayStart = ServerTime.todayStart
    val todayEnd = ServerTime.todayEnd
    // don't log more than once a day
    val attendanceSheetSignedUUID = sql"""
			  select uuid from AttendanceSheetSigned
			  where personUUID=${event.getPersonUUID()}
			  and institutionUUID=${event.getInstitutionUUID()}
			  and eventFiredAt between ${todayStart} and ${todayEnd}
		  """.first
    if (!attendanceSheetSignedUUID.isDefined)
      sql"""
		    insert into AttendanceSheetSigned(uuid,eventFiredAt,institutionUUID,personUUID)
		    values(${event.getUUID}, ${event.getEventFiredAt}, ${event.getInstitutionUUID}, ${event.getPersonUUID});
			""".executeUpdate

  }

  def logEnrollmentStateChanged(uuid: String, eventFiredAt: String, fromPersonUUID: String,
    enrollmentUUID: String, fromState: EnrollmentState, toState: EnrollmentState, sendEmail: Boolean) = {

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

    if (EnrollmentState.enrolled.equals(toState) && sendEmail) {
      val enrollment = EnrollmentRepo(enrollmentUUID).get
      val person = PersonRepo(enrollment.getPersonUUID).get
      if (person.getEmail != null && !"true".equals(Settings.get("TEST_MODE").orNull)) {
        if (enrollment.getCourseClassUUID != null) {
          val courseClass = CourseClassesRepo(enrollment.getCourseClassUUID).get
		  val course = CoursesRepo.byCourseClassUUID(courseClass.getUUID).get
          val institution = InstitutionsRepo.byUUID(courseClass.getInstitutionUUID).get
          EmailService.sendEmailEnrolled(person, institution, course)
        } else {
          val courseVersion = CourseVersionRepo(enrollment.getCourseVersionUUID).get
          val course = CourseRepo(courseVersion.getCourseUUID).get
          val institution = InstitutionsRepo.byUUID(course.getInstitutionUUID).get
          EmailService.sendEmailEnrolled(person, institution, course)
        }
      }
    }
  }

  def logEnrollmentStateChanged(event: EnrollmentStateChanged): Unit =
    logEnrollmentStateChanged(event.getUUID, event.getEventFiredAt, event.getFromPersonUUID,
      event.getEnrollmentUUID, event.getFromState, event.getToState, true)

  def logCourseClassStateChanged(uuid: String, eventFiredAt: String, fromPersonUUID: String,
    courseClassUUID: String, fromState: CourseClassState, toState: CourseClassState) = {

    sql"""insert into CourseClassStateChanged(uuid,eventFiredAt,personUUID,courseClassUUID,fromState,toState)
	    values(${uuid},
			   ${eventFiredAt},
         ${fromPersonUUID},
         ${courseClassUUID},
         ${fromState.toString},
			   ${toState.toString});
		""".executeUpdate

    sql"""update CourseClass set state = ${toState.toString} where uuid = ${courseClassUUID};
		""".executeUpdate
		
  }

  def logCourseClassStateChanged(event: CourseClassStateChanged): Unit =
    logCourseClassStateChanged(event.getUUID, event.getEventFiredAt, event.getFromPersonUUID,
      event.getCourseClassUUID, event.getFromState, event.getToState)

}