package kornell.server.test.api

import org.junit.runner.RunWith
import org.jboss.arquillian.junit.Arquillian
import kornell.server.test.KornellSuite
import kornell.server.test.Mocks
import javax.inject.Inject
import org.junit.Test
import kornell.server.jdbc.repository.EnrollmentsRepo
import kornell.server.repository.Entities
import kornell.server.api.EnrollmentResource
import kornell.core.entity.Enrollment
import kornell.core.entity.EnrollmentState
import kornell.core.entity.CourseClass
import kornell.core.entity.Person
import kornell.server.util.Err

@RunWith(classOf[Arquillian])
class EnrollmentResourceSuite extends KornellSuite {
  @Inject var enrollmentsRepo: EnrollmentsRepo = _
  @Inject var enrollmentResource: EnrollmentResource = _
  @Inject var mocks: Mocks = _

  //TODO: Smell
  def createEnrollment(
      cc: CourseClass = mocks.newCourseClassEmail,
      p:Person = mocks.student): Enrollment =
    enrollmentsRepo.create(
      Entities.newEnrollment(
        uuid = randUUID,
        personUUID = p.getUUID,
        courseClassUUID = cc.getUUID,
        state = EnrollmentState.requested))

  @Test def platformAdminCanUpdateAnEnrollment = runAs(mocks.platfAdm) {
    val newNotes = randStr
    val enrollment = createEnrollment()
    enrollment.setNotes(newNotes)
    val newEnrollment: Enrollment = enrollmentResource.update(enrollment)
    assert(newNotes == newEnrollment.getNotes)
  }

  @Test def institutionAdminCanUpdateAnEnrollment = runAs(mocks.ittAdm) {
    val notes = randStr
    val enrollment = createEnrollment()
    enrollment.setNotes(notes)
    val newEnrollment = enrollmentResource.update(enrollment).asInstanceOf[Enrollment]
    assert(notes.equals(newEnrollment.getNotes))
  }

  @Test def studentCanUpdateHisEnrollment = runAs(mocks.student) {    
    val notes = randStr
    val enrollment = mocks.enrollment
    enrollment.setNotes(notes)
    val newEnrollment = enrollmentResource.update(enrollment)
    assert(notes.equals(newEnrollment.getNotes))
  }
  
  
  @Test(expected=classOf[Err]) def studentCanNotUpdateEnrollmentOfOthers = runAs(mocks.student) {
    val cc = mocks.newCourseClassEmail
    val enrollment = createEnrollment(cc,mocks.ittAdm)
    enrollment.setNotes(randStr)
    enrollmentResource.update(enrollment)
  }
}