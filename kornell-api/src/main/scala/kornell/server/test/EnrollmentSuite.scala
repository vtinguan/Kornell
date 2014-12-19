package kornell.server.test

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import javax.inject.Inject
import kornell.server.content.ContentManagers
import org.jboss.arquillian.junit.Arquillian
import kornell.server.helper.GenPlatformAdmin
import kornell.server.helper.GenInstitutionAdmin
import kornell.server.helper.GenCourseClass
import kornell.server.jdbc.repository.EnrollmentsRepo
import kornell.server.repository.Entities
import kornell.core.entity.EnrollmentState
import kornell.server.api.EnrollmentResource
import javax.enterprise.inject.Instance
import kornell.server.cdi.EmailCourseClass
import kornell.core.entity.CourseClass
import kornell.core.entity.Person
import kornell.server.test.producers.CourseVersionMocks
import org.junit.BeforeClass
import org.junit.AfterClass
import kornell.server.helper.Generator

@RunWith(classOf[Arquillian])
class EnrollmentSuite extends KornellSuite {

  @Inject
  var enrollmentsRepo: EnrollmentsRepo = _

  @Inject
  var enrollmentRepo: EnrollmentsRepo = _

  @Inject
  var enrollmentRes: EnrollmentResource = _

  @Inject
  var mocks: CourseVersionMocks = _

  @Test def NotesShouldPersistWithEnrollment = runAs(mocks.student) {
    val notes = randStr
    mocks.enrollment.setNotes(notes)
    val updatedEnroll = enrollmentRes.update(mocks.enrollment)
    assertEquals(notes, updatedEnroll.getNotes)
  }

  /*
  
  
  "The institutionAdmin" should "be able to update an enrollment" in asInstitutionAdmin {
    val courseClass = newCourseClassEmail
    val person = newPerson
    val x = "NEW NOTES2"
	  val enrollment = EnrollmentsRepo.create(Entities.newEnrollment(randUUID, null, courseClass.getUUID, person.getUUID, null, "", EnrollmentState.requested,null,null,null,null,null))
	  val enrollmentResource:EnrollmentResource = ??? // new EnrollmentResource(enrollment.getUUID)
    enrollment.setNotes(x)
    val newEnrollment = enrollmentResource.update(enrollment).asInstanceOf[Enrollment]
    assert(x == newEnrollment.getNotes)
  }

  "A user" should "not be able to update an enrollment that doesn't belong to him" in asInstitutionAdmin {
    val courseClass = newCourseClassEmail
    val enrollment = EnrollmentsRepo.create(Entities.newEnrollment(randUUID, null, courseClass.getUUID, personUUID, null, "", EnrollmentState.requested,null,null,null,null,null))
    asIdentity(newPerson.getUUID) {
      val x = "NEW NOTES4"
      val er:EnrollmentResource = ???
	  val enrollmentResource = er //new EnrollmentResource(enrollment.getUUID)
    enrollment.setNotes(x)
    var newEnrollment:Enrollment = null
    try {
    	newEnrollment = enrollmentResource.update(enrollment)
    } catch {
      case ise:IllegalStateException => {assert(newEnrollment == null)}
    }
    }
    
  } 
*/
}