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
import scala.collection.JavaConverters._
import kornell.server.helper.GenInstitutionAdmin
import kornell.core.entity.RegistrationEnrollmentType

@RunWith(classOf[JUnitRunner])
class EnrollmentsSpec 
	extends UnitSpec 
	with GenInstitution
	with GenPlatformAdmin
	with GenInstitutionAdmin
	with GenCourseClass {
  
  "The platformAdmin" should 
  "be able to register and enroll participants with the email" in asPlatformAdmin {
    val fullName = randName
    val email = randEmail
    val courseClass = newCourseClassEmail
    val enrollmentRequestsTO = TOs.newEnrollmentRequestsTO(new ArrayList[EnrollmentRequestTO]) 
    val totalEnrollments = 10
    for(i <- 1 to totalEnrollments){
    	enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, i+fullName, i+email, null, RegistrationEnrollmentType.email, false))
    }
    EnrollmentsResource().putEnrollments(enrollmentRequestsTO)
     
    val enrollmentsCreated = EnrollmentsRepo.byCourseClass(courseClass.getUUID)
    assert(enrollmentsCreated.getEnrollmentTOs.size == totalEnrollments)
    //assert(mockHttpServletResponse.getStatus == 0)
  }  
  
  "The platformAdmin" should "be able to register and enroll participants with the cpf" in asPlatformAdmin {
    val fullName = randStr
    val cpf = randCPF
    val courseClass = newCourseClassCpf
    val enrollmentRequestsTO = TOs.newEnrollmentRequestsTO(new ArrayList[EnrollmentRequestTO])   
    val totalEnrollments = 10
    for(i <- 1 to totalEnrollments){
    	enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, i+fullName, i+cpf, "hunter2", RegistrationEnrollmentType.cpf, false))
    } 
    EnrollmentsResource().putEnrollments(enrollmentRequestsTO)
    
    val enrollmentsCreated = EnrollmentsRepo.byCourseClass(courseClass.getUUID)
    assert(enrollmentsCreated.getEnrollmentTOs.size == totalEnrollments)
    //assert(mockHttpServletResponse.getStatus == 0)
  }
  
  "The platformAdmin" should "not be able to register participants with duplicate emails or cpfs" in asPlatformAdmin {
    val enrollmentRequestsTO = TOs.newEnrollmentRequestsTO(new ArrayList[EnrollmentRequestTO])    
    val courseClassEmail = newCourseClassEmail
    val courseClassCpf = newCourseClassCpf
    val fullName= randStr
    val email = randEmail
    val cpf = randCPF
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClassEmail.getUUID, fullName, email, null, RegistrationEnrollmentType.email, false))
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClassCpf.getUUID, fullName, cpf, "hunter2", RegistrationEnrollmentType.cpf, false))
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClassEmail.getUUID, fullName, email, null, RegistrationEnrollmentType.email, false))
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClassCpf.getUUID, fullName, cpf, "hunter2", RegistrationEnrollmentType.cpf, false))
    EnrollmentsResource().putEnrollments(enrollmentRequestsTO)
     
    val enrollmentsCreatedEmail = EnrollmentsRepo.byCourseClass(courseClassEmail.getUUID)
    assert(enrollmentsCreatedEmail.getEnrollmentTOs.size == 1)
    val enrollmentsCreatedCpf = EnrollmentsRepo.byCourseClass(courseClassCpf.getUUID)
    assert(enrollmentsCreatedCpf.getEnrollmentTOs.size == 1)
    //assert(mockHttpServletResponse.getStatus == 0)
  }
  
  
  
  "The institutionAdmin" should "be able to register and enroll one participant with the email" in asInstitutionAdmin {
    val courseClass = newCourseClassEmail
    val fullName = randStr
    val email = randEmail
    val enrollmentRequestsTO = TOs.newEnrollmentRequestsTO(new ArrayList[EnrollmentRequestTO])  
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, "institutionAdmin"+fullName, "institutionAdmin"+email, null, RegistrationEnrollmentType.email, false))
    EnrollmentsResource().putEnrollments(enrollmentRequestsTO)
    
    val enrollmentsCreated = EnrollmentsRepo.byCourseClass(courseClass.getUUID)
    assert(enrollmentsCreated.getEnrollmentTOs.size == 1)
    assert({ 
      enrollmentsCreated.getEnrollmentTOs.asScala exists(e => e.getPerson.getFullName.equals("institutionAdmin"+fullName))
    })
//    assert(mockHttpServletResponse.getStatus == 0)
  }  
  /*
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
 */
/*  
  "A user" should "be able to request enrollment to a class" in asPerson {
    val courseClass = newCourseClass
    val enrollmentRequestsTO = TOs.newEnrollmentRequestsTO(new ArrayList[EnrollmentRequestTO])
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, "person"+person.getFullName, "institutionAdmin"+person.getEmail, null, false))
    EnrollmentsResource().putEnrollments(enrollmentRequestsTO)
    
    val enrollmentsCreated = EnrollmentsRepo.byCourseClass(courseClass.getUUID)
    assert(enrollmentsCreated.getEnrollmentTOs.size == 1)
    assert({ 
      enrollmentsCreated.getEnrollmentTOs.asScala exists(e => e.getPerson.getFullName.equals(person.getFullName))
    	})
//    assert(mockHttpServletResponse.getStatus == 0)
  }
  */
}