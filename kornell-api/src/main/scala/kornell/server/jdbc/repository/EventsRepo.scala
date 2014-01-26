package kornell.server.jdbc.repository

import kornell.core.event.EnrollmentStateChanged
import com.google.web.bindery.autobean.vm.AutoBeanFactorySource
import kornell.core.event.EventFactory
import kornell.core.event.EnrollmentStateChanged
import java.util.Date
import kornell.core.entity.EnrollmentState
import kornell.server.util.EmailService
import kornell.core.entity.CourseClass
import kornell.core.entity.Enrollment
import kornell.server.jdbc.SQL._ 
import kornell.core.event.ActomEntered


object EventsRepo {
  val events = AutoBeanFactorySource.create(classOf[EventFactory])
  
  def newEnrollmentStateChanged = events.newEnrollmentStateChanged.as
  
  def logActomEntered(event: ActomEntered) = sql"""
    insert into ActomEntered(uuid,eventFiredAt,enrollmentUUID,actomKey)
    values(${event.getUUID},
  		   ${event.getEventFiredAt},
           ${event.getEnrollmentUUID},
		   ${event.getActomKey});
	""".executeUpdate

	
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
	    val enrollment = EnrollmentsRepo.byUUID(enrollmentUUID).get
	    val courseClass = CourseClassesRepo(enrollment.getCourseClassUUID).get
	    val course = CoursesRepo.byCourseClassUUID(courseClass.getUUID).get
	    val institution = InstitutionsRepo.byUUID(courseClass.getInstitutionUUID).get
	    EmailService.sendEmailEnrolled(enrollment.getPerson, institution, course)
	  }
  }
	
  def logEnrollmentStateChanged(event: EnrollmentStateChanged):Unit = 
    logEnrollmentStateChanged(event.getUUID,event.getEventFiredAt,event.getFromPersonUUID,
        event.getEnrollmentUUID,event.getFromState,event.getToState)
	
}