package kornell.server.api

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import kornell.core.entity.Enrollment


@RunWith(classOf[JUnitRunner])
class EnrollmentSpec {   
/*extends UnitSpec  
	with GenPlatformAdmin 
	with GenInstitutionAdmin
	with GenCourseClass {

  "The platformAdmin" should "be able to update an enrollment" in asPlatformAdmin {
    val courseClass = newCourseClass
    val x = "NEW NOTES"
	  val enrollment = EnrollmentsRepo.create(
	      Entities.newEnrollment(randUUID, null, courseClass.getUUID, null, null, "", EnrollmentState.requested,null,null,null,null,null))
	  val enrollmentResource = new EnrollmentResource(???,enrollment.getUUID)
    enrollment.setNotes(x)
    val newEnrollment = enrollmentResource.update(enrollment).asInstanceOf[Enrollment]
    assert(x == newEnrollment.getNotes)
  }
*/  
  /*
  "The institutionAdmin" should "be able to update an enrollment" in {
    //assumeIdentity(institutionAdminUUID)
    val x = "NEW NOTES2"
	  val enrollment = EnrollmentsRepo.create(Entities.newEnrollment(randUUID, null, courseClass.getUUID, null, null, "", EnrollmentState.requested,null,null,null,null,null))
	  val enrollmentResource = new EnrollmentResource(enrollment.getUUID)
    enrollment.setNotes(x)
    val newEnrollment = enrollmentResource.update(enrollment).asInstanceOf[Enrollment]
    assert(x.equals(newEnrollment.getNotes))
  }
  
  "A user" should "be able to update his own enrollment" in {
    val x = "NEW NOTES3"
	  val enrollment = EnrollmentsRepo.create(Entities.newEnrollment(randUUID, null, courseClass.getUUID, null, null, "", EnrollmentState.requested,null,null,null,null,null))
	  val enrollmentResource = new EnrollmentResource(enrollment.getUUID)
    enrollment.setNotes(x)
    //val newEnrollment = enrollmentResource.update(notAnAdminSecurityContext, mockHttpServletResponse, enrollment).asInstanceOf[Enrollment]
  //  assert(x.equals(newEnrollment.getNotes))
  }
  
  "A user" should "not be able to update an enrollment that doesn't belong to him" in {
  //  val enrollment = EnrollmentsRepo.create(Entities.newEnrollment(randUUID, null, courseClass.getUUID, institutionAdmin.getUUID, null, "", EnrollmentState.requested,null,null,null,null,null))
    val x = "NEW NOTES4"
	 // val enrollmentResource = new EnrollmentResource(enrollment.getUUID)
    //enrollment.setNotes(x)
    //enrollmentResource.update(notAnAdminSecurityContext, mockHttpServletResponse, enrollment)
    //assert(mockHttpServletResponse.getStatus != 0)
  } 
  */
}