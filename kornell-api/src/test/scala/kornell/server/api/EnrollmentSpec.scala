package kornell.server.api

import org.junit.runner.RunWith
import javax.ws.rs.Consumes
import javax.ws.rs.PUT
import javax.ws.rs.Produces
import kornell.core.entity.Enrollment
import kornell.core.entity.EnrollmentState
import kornell.server.helper.GenCourseClass
import kornell.server.helper.GenInstitutionAdmin
import kornell.server.helper.GenPlatformAdmin
import kornell.server.jdbc.repository.EnrollmentsRepo
import kornell.server.repository.Entities
import kornell.server.test.UnitSpec
import org.scalatest.junit.JUnitRunner


@RunWith(classOf[JUnitRunner])
class EnrollmentSpec extends UnitSpec  
	with GenPlatformAdmin 
	with GenInstitutionAdmin
	with GenCourseClass {
  

  "The platformAdmin" should "be able to update an enrollment" in asPlatformAdmin {
    val courseClass = newCourseClassEmail
    val person = newPerson
    val x = "NEW NOTES"
	  val enrollment = EnrollmentsRepo.create(
	      Entities.newEnrollment(randUUID, null, courseClass.getUUID, person.getUUID, null, "", EnrollmentState.requested,null,null,null,null,null))
	  val enrollmentResource = new EnrollmentResource(enrollment.getUUID)
    enrollment.setNotes(x)
    val newEnrollment = enrollmentResource.update(enrollment).asInstanceOf[Enrollment]
    assert(x == newEnrollment.getNotes)
  }
  
  
  "The institutionAdmin" should "be able to update an enrollment" in asInstitutionAdmin {
    val courseClass = newCourseClassEmail
    val person = newPerson
    val x = "NEW NOTES2"
	  val enrollment = EnrollmentsRepo.create(Entities.newEnrollment(randUUID, null, courseClass.getUUID, person.getUUID, null, "", EnrollmentState.requested,null,null,null,null,null))
	  val enrollmentResource = new EnrollmentResource(enrollment.getUUID)
    enrollment.setNotes(x)
    val newEnrollment = enrollmentResource.update(enrollment).asInstanceOf[Enrollment]
    assert(x == newEnrollment.getNotes)
  }

  "A user" should "not be able to update an enrollment that doesn't belong to him" in asInstitutionAdmin {
    val courseClass = newCourseClassEmail
    val enrollment = EnrollmentsRepo.create(Entities.newEnrollment(randUUID, null, courseClass.getUUID, personUUID, null, "", EnrollmentState.requested,null,null,null,null,null))
    asIdentity(newPerson.getUUID) {
      val x = "NEW NOTES4"
	  val enrollmentResource = new EnrollmentResource(enrollment.getUUID)
    enrollment.setNotes(x)
    var newEnrollment:Enrollment = null
    try {
    	newEnrollment = enrollmentResource.update(enrollment)
    } catch {
      case ise:IllegalStateException => {assert(newEnrollment == null)}
    }
    }
    
  } 
  
}