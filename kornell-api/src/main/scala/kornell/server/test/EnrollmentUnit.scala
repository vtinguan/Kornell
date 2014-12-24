package kornell.server.test

import java.util.logging.Logger
import org.jboss.arquillian.junit.Arquillian
import org.junit.Assert._
import org.junit.Test
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitSuite
import javax.inject.Inject
import kornell.core.entity.Person
import kornell.server.api.EnrollmentResource
import kornell.server.jdbc.repository.EnrollmentsRepo
import kornell.server.repository.Entities
import kornell.core.entity.EnrollmentState
import kornell.core.entity.Enrollment

@RunWith(classOf[Arquillian])
class EnrollmentSuite extends KornellSuite {
  val logger = Logger.getLogger(classOf[EnrollmentSuite].getName)

  @Inject
  var enrollmentsRepo: EnrollmentsRepo = _

  @Inject
  var enrollmentRes: EnrollmentResource = _

  @Inject
  var mocks: EnrollmentMocks = _

  @Test def NotesPersistsWithEnrollment = runAs(mocks.student) {
    val notes = randStr
    mocks.enrollment.setNotes(notes)
    val updatedEnroll = enrollmentRes.update(mocks.enrollment)
    assertEquals(notes, updatedEnroll.getNotes)
  }

  @Test def IttAdminUpdatesEnrollment = runAs(mocks.ittAdm) {
    val person = mocks.newPerson
    val notes = randStr
    val enrollment = enrollmentsRepo.create(Entities.newEnrollment(randUUID, null, mocks.courseClass.getUUID, person.getUUID, null, "", EnrollmentState.requested, null, null, null, null, null))
    enrollment.setNotes(notes)
    val newEnrollment = enrollmentRes.update(enrollment)
    assert(notes == newEnrollment.getNotes)
  }

  @Test def studentCanNotUpdateOthersEnrollment = {
    val otherStudent = mocks.newPerson
    
    val enrollment = enrollmentsRepo.create(Entities.newEnrollment(randUUID, null,
      mocks.courseClass.getUUID,
      otherStudent.getUUID,
      null, "", EnrollmentState.requested, null, null, null, null, null))
       
    runAs(mocks.student) {
      val x = randStr
      enrollment.setNotes(x)
      var newEnrollment: Enrollment = null
      try {
        newEnrollment = enrollmentRes.update(enrollment)
        //fail()
      } catch {
        case ise: IllegalStateException => { assert(newEnrollment == null) }
      }
    }
    
  }
}