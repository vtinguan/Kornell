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
class EnrollmentsSpec extends UnitSpec with SimpleInstitution {
  
  "The platformAdmin" should "be able to register and enroll participants with the email" in {
    val enrollmentRequestsTO = TOs.newEnrollmentRequestsTO(new ArrayList[EnrollmentRequestTO]) 
    val totalEnrollments = 10
    for(i <- 1 to totalEnrollments){
    	enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, i+fullName, i+email, null))
    }
    enrollmentsResource.putEnrollments(platformAdminSecurityContext, mockHttpServletResponse, enrollmentRequestsTO)
    
    val enrollmentsCreated = EnrollmentsRepo.byCourseClass(courseClass.getUUID)
    assert(enrollmentsCreated.getEnrollments.size == totalEnrollments)
    assert(mockHttpServletResponse.getStatus == 0)
  }  
  
  "The platformAdmin" should "be able to register and enroll participants with the cpf" in {
    val enrollmentRequestsTO = TOs.newEnrollmentRequestsTO(new ArrayList[EnrollmentRequestTO])   
    val totalEnrollments = 10
    for(i <- 1 to totalEnrollments){
    	enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, i+fullName, null, i+cpf))
    } 
    enrollmentsResource.putEnrollments(platformAdminSecurityContext, mockHttpServletResponse, enrollmentRequestsTO)
    
    val enrollmentsCreated = EnrollmentsRepo.byCourseClass(courseClass.getUUID)
    assert(enrollmentsCreated.getEnrollments.size == totalEnrollments)
    assert(mockHttpServletResponse.getStatus == 0)
  }
  
  "The platformAdmin" should "not be able to register participants with duplicate emails or cpfs" in {
    val enrollmentRequestsTO = TOs.newEnrollmentRequestsTO(new ArrayList[EnrollmentRequestTO])    
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, fullName, email, null))
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, fullName, null, cpf))
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, fullName, email, null))
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, fullName, null, cpf))
    enrollmentsResource.putEnrollments(platformAdminSecurityContext, mockHttpServletResponse, enrollmentRequestsTO)
    
    val enrollmentsCreated = EnrollmentsRepo.byCourseClass(courseClass.getUUID)
    assert(enrollmentsCreated.getEnrollments.size == 2)
    assert(mockHttpServletResponse.getStatus == 0)
  }
  
  "The institutionAdmin" should "be able to register and enroll one participant with the email" in {
    val enrollmentRequestsTO = TOs.newEnrollmentRequestsTO(new ArrayList[EnrollmentRequestTO])    
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, "institutionAdmin"+fullName, "institutionAdmin"+email, null))
    enrollmentsResource.putEnrollments(institutionAdminSecurityContext, mockHttpServletResponse, enrollmentRequestsTO)
    
    val enrollmentsCreated = EnrollmentsRepo.byCourseClass(courseClass.getUUID)
    assert(enrollmentsCreated.getEnrollments.size == 1)
    assert({ 
      enrollmentsCreated.getEnrollments.asScala exists(e => e.getPerson.getFullName.equals("institutionAdmin"+fullName))
    })
    assert(mockHttpServletResponse.getStatus == 0)
  }  
  
  "A user that's not a platform or institutionAdmin" should "not be able to register and enroll one participant" in {
    val enrollmentRequestsTO = TOs.newEnrollmentRequestsTO(new ArrayList[EnrollmentRequestTO])    
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, "notAnAdmin"+fullName, "notAnAdmin"+email, null))
    enrollmentsResource.putEnrollments(notAnAdminSecurityContext, mockHttpServletResponse, enrollmentRequestsTO)
    
    val enrollmentsCreated = EnrollmentsRepo.byCourseClass(courseClass.getUUID)
    assert(enrollmentsCreated.getEnrollments.size == 0)
    assert(!{ 
      enrollmentsCreated.getEnrollments.asScala exists(e => e.getPerson.getFullName.equals("notAnAdmin"+fullName))
    	})
    assert(mockHttpServletResponse.getStatus != 0)
  }  
  
  "A user" should "be able to request enrollment to a class" in {
    val enrollment = Entities.newEnrollment(randUUID, null, courseClass.getUUID, notAnAdmin.getUUID, null, null, EnrollmentState.requested)
    enrollmentsResource.create(notAnAdminSecurityContext, enrollment)
    
    val enrollmentsCreated = EnrollmentsRepo.byCourseClass(courseClass.getUUID)
    assert(enrollmentsCreated.getEnrollments.size == 1)
    assert({ 
      enrollmentsCreated.getEnrollments.asScala exists(e => e.getPerson.getFullName.equals(notAnAdmin.getFullName))
    	})
    assert(mockHttpServletResponse.getStatus == 0)
  }  
  
}