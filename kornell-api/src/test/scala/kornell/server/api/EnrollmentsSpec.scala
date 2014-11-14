package kornell.server.api

import java.util.ArrayList
import org.junit.runner.RunWith
import kornell.core.to.EnrollmentRequestTO
import kornell.server.helper.SimpleInstitution
import kornell.server.jdbc.repository.EnrollmentsRepo
import kornell.server.repository.TOs
import kornell.server.test.UnitSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.junit.JUnitRunner
import kornell.server.helper.GenInstitution
import kornell.server.helper.GenPlatformAdmin
import kornell.server.helper.GenCourseClass
import kornell.core.entity.RegistrationEnrollmentType


@RunWith(classOf[JUnitRunner])
class EnrollmentsSpec 
	extends UnitSpec 
	with GenInstitution
	with GenPlatformAdmin
	with GenCourseClass {
  
  "The platformAdmin" should 
  "be able to register and enroll participants with the email" in asPlatformAdmin {
    val fullName = randName
    val email = randEmail
    val courseClass = newCourseClass
    val enrollmentRequestsTO = TOs.newEnrollmentRequestsTO(new ArrayList[EnrollmentRequestTO]) 
    val totalEnrollments = 10
    for(i <- 1 to totalEnrollments){
    	enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, i+fullName, i+email, null, RegistrationEnrollmentType.email))
    }
    EnrollmentsResource().putEnrollments(enrollmentRequestsTO)
     
    val enrollmentsCreated = EnrollmentsRepo.byCourseClass(courseClass.getUUID)
    //assert(enrollmentsCreated.getEnrollments.size == totalEnrollments)
    //assert(mockHttpServletResponse.getStatus == 0)
  }  
  
  //TODO
  /*
  "The platformAdmin" should "be able to register and enroll participants with the cpf" in {
    val fullName = randStr
    val cpf = randCPF
    val courseClass = newCourseClass
    val enrollmentRequestsTO = TOs.newEnrollmentRequestsTO(new ArrayList[EnrollmentRequestTO])   
    val totalEnrollments = 10
    for(i <- 1 to totalEnrollments){
    	enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, i+fullName, null, i+cpf))
    } 
    EnrollmentsResource().putEnrollments(enrollmentRequestsTO)
    
    val enrollmentsCreated = EnrollmentsRepo.byCourseClass(courseClass.getUUID)
    //assert(enrollmentsCreated.getEnrollments.size == totalEnrollments)
    //assert(mockHttpServletResponse.getStatus == 0)
  }
  */
  //TODO
  /*
  "The platformAdmin" should "not be able to register participants with duplicate emails or cpfs" in {
    val enrollmentRequestsTO = TOs.newEnrollmentRequestsTO(new ArrayList[EnrollmentRequestTO])    
    val courseClass = newCourseClass
    val fullName= randStr
    val email = randEmail
    val cpf = randCPF
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, fullName, email, null))
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, fullName, null, cpf))
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, fullName, email, null))
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, fullName, null, cpf))
    EnrollmentsResource().putEnrollments(enrollmentRequestsTO)
     
    val enrollmentsCreated = EnrollmentsRepo.byCourseClass(courseClass.getUUID)
    //assert(enrollmentsCreated.getEnrollments.size == 2)
    //assert(mockHttpServletResponse.getStatus == 0)
  }
  
  */
  
  /*"The institutionAdmin" should "be able to register and enroll one participant with the email" in {
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
    val enrollment = Entities.newEnrollment(randUUID, null, courseClass.getUUID, notAnAdmin.getUUID, null, null, EnrollmentState.requested,null,null,null,null,null)
    enrollmentsResource.create(notAnAdminSecurityContext, enrollment)
    
    val enrollmentsCreated = EnrollmentsRepo.byCourseClass(courseClass.getUUID)
    assert(enrollmentsCreated.getEnrollments.size == 1)
    assert({ 
      enrollmentsCreated.getEnrollments.asScala exists(e => e.getPerson.getFullName.equals(notAnAdmin.getFullName))
    	})
    assert(mockHttpServletResponse.getStatus == 0)
  }  */
  
}