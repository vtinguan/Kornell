package kornell.server.api

import java.util.ArrayList
import java.util.Date
import scala.collection.JavaConverters.asScalaBufferConverter
import org.junit.runner.RunWith
import org.scalatest.BeforeAndAfter
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.Produces
import kornell.core.entity.CourseClass
import kornell.core.entity.Enrollment
import kornell.core.entity.EnrollmentState
import kornell.core.entity.RoleType
import kornell.core.to.EnrollmentRequestTO
import kornell.server.jdbc.SQL.SQLHelper
import kornell.server.jdbc.repository.CourseVersionsRepo
import kornell.server.jdbc.repository.CoursesRepo
import kornell.server.jdbc.repository.EnrollmentsRepo
import kornell.server.jdbc.repository.InstitutionsRepo
import kornell.server.jdbc.repository.PeopleRepo
import kornell.server.jdbc.repository.PersonRepo
import kornell.server.repository.Entities
import kornell.server.repository.TOs
import kornell.server.test.UnitSpec
import org.scalatest.junit.JUnitRunner
import kornell.server.jdbc.repository.CourseClassesRepo
import kornell.server.helper.SimpleInstitution


@RunWith(classOf[JUnitRunner])
class EnrollmentSpec extends UnitSpec with SimpleInstitution {
    
  "The platformAdmin" should "be able to update an enrollment" in {
    val x = "NEW NOTES"
	  val enrollment = EnrollmentsRepo.create(Entities.newEnrollment(randUUID, null, courseClass.getUUID, notAnAdmin.getUUID, null, "", EnrollmentState.requested))
	  val enrollmentResource = new EnrollmentResource(enrollment.getUUID)
    enrollment.setNotes(x)
    val newEnrollment = enrollmentResource.update(platformAdminSecurityContext, mockHttpServletResponse, enrollment).asInstanceOf[Enrollment]
    assert(x.equals(newEnrollment.getNotes))
  }
  
  "The institutionAdmin" should "be able to update an enrollment" in {
    val x = "NEW NOTES2"
	  val enrollment = EnrollmentsRepo.create(Entities.newEnrollment(randUUID, null, courseClass.getUUID, notAnAdmin.getUUID, null, "", EnrollmentState.requested))
	  val enrollmentResource = new EnrollmentResource(enrollment.getUUID)
    enrollment.setNotes(x)
    val newEnrollment = enrollmentResource.update(institutionAdminSecurityContext, mockHttpServletResponse, enrollment).asInstanceOf[Enrollment]
    assert(x.equals(newEnrollment.getNotes))
  }
  
  "A user" should "be able to update his own enrollment" in {
    val x = "NEW NOTES3"
	  val enrollment = EnrollmentsRepo.create(Entities.newEnrollment(randUUID, null, courseClass.getUUID, notAnAdmin.getUUID, null, "", EnrollmentState.requested))
	  val enrollmentResource = new EnrollmentResource(enrollment.getUUID)
    enrollment.setNotes(x)
    val newEnrollment = enrollmentResource.update(notAnAdminSecurityContext, mockHttpServletResponse, enrollment).asInstanceOf[Enrollment]
    assert(x.equals(newEnrollment.getNotes))
  }
  
  "A user" should "not be able to update an enrollment that doesn't belong to him" in {
    val enrollment = EnrollmentsRepo.create(Entities.newEnrollment(randUUID, null, courseClass.getUUID, institutionAdmin.getUUID, null, "", EnrollmentState.requested))
    val x = "NEW NOTES4"
	  val enrollmentResource = new EnrollmentResource(enrollment.getUUID)
    enrollment.setNotes(x)
    enrollmentResource.update(notAnAdminSecurityContext, mockHttpServletResponse, enrollment)
    assert(mockHttpServletResponse.getStatus != 0)
  } 
  
}