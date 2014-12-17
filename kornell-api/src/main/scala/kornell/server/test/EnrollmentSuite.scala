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

@RunWith(classOf[Arquillian])
class EnrollmentSuite
  extends JUnitSuite
 // with GenPlatformAdmin
 // with GenInstitutionAdmin
 // with GenCourseClass 
  {

  @Inject
  var enrollmentsRepo:EnrollmentsRepo = _
  
  @Inject
  var enrollmentRepo:EnrollmentsRepo = _
  
  @Inject
  var enrollmentRes: EnrollmentResource = _
  
  @Inject @EmailCourseClass
  var emailCourseClassBean:Instance[CourseClass] = _
  

  @Test def NotesShouldBersistWithEnrollment {
   val courseClass = emailCourseClassBean.get
   val courseClass2 = emailCourseClassBean.get
   println("CHECK")
   // val person = newPerson
   // val x = "NEW NOTES"
   // val enrollment = enrollmentsRepo.create(
   //   Entities.newEnrollment(randUUID, null, courseClass.getUUID, person.getUUID, null, "", EnrollmentState.requested, null, null, null, null, null))
   // enrollment.setNotes(x)
   // val newEnrollment = enrollmentRes.update(enrollment)
   //  assertEquals(x,newEnrollment.getNotes)
  }
  
  //	with UnitSpec 

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

object EnrollmentSuite {

}