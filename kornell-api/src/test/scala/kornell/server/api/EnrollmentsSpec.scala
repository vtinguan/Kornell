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
import kornell.core.entity.RegistrationType
import kornell.core.entity.EnrollmentState
import kornell.server.jdbc.repository.EnrollmentRepo
import kornell.server.repository.Entities
import kornell.server.util.RequirementNotMet

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
    	enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, i+fullName, i+email, null, RegistrationType.email, null, false))
    }
    EnrollmentsResource().putEnrollments(enrollmentRequestsTO)
     
    val enrollmentsCreated = EnrollmentsRepo.byCourseClass(courseClass.getUUID)
    assert(enrollmentsCreated.getEnrollmentTOs.size == totalEnrollments)
  }
  
  "The platformAdmin" should "be able to register and enroll participants with the cpf" in asPlatformAdmin {
    val fullName = randStr
    val cpf = randCPF
    val courseClass = newCourseClassCpf
    val enrollmentRequestsTO = TOs.newEnrollmentRequestsTO(new ArrayList[EnrollmentRequestTO])   
    val totalEnrollments = 10
    for(i <- 1 to totalEnrollments){
    	enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, i+fullName, i+cpf, "hunter2", RegistrationType.cpf, null, false))
    } 
    EnrollmentsResource().putEnrollments(enrollmentRequestsTO)
    
    val enrollmentsCreated = EnrollmentsRepo.byCourseClass(courseClass.getUUID)
    assert(enrollmentsCreated.getEnrollmentTOs.size == totalEnrollments)
  }
  
  "The platformAdmin" should "not be able to register participants with duplicate emails or cpfs" in asPlatformAdmin {
    val enrollmentRequestsTO = TOs.newEnrollmentRequestsTO(new ArrayList[EnrollmentRequestTO])    
    val courseClassEmail = newCourseClassEmail
    val courseClassCpf = newCourseClassCpf
    val fullName= randStr
    val email = randEmail
    val cpf = randCPF
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClassEmail.getUUID, fullName, email, null, RegistrationType.email, null, false))
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClassCpf.getUUID, fullName, cpf, "hunter2", RegistrationType.cpf, null, false))
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClassEmail.getUUID, fullName, email, null, RegistrationType.email, null, false))
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClassCpf.getUUID, fullName, cpf, "hunter2", RegistrationType.cpf, null, false))
    EnrollmentsResource().putEnrollments(enrollmentRequestsTO)
     
    val enrollmentsCreatedEmail = EnrollmentsRepo.byCourseClass(courseClassEmail.getUUID)
    assert(enrollmentsCreatedEmail.getEnrollmentTOs.size == 1)
    val enrollmentsCreatedCpf = EnrollmentsRepo.byCourseClass(courseClassCpf.getUUID)
    assert(enrollmentsCreatedCpf.getEnrollmentTOs.size == 1)
  }
  
  "The institutionAdmin" should "be able to register and enroll one participant with the email" in asInstitutionAdmin {
    val courseClass = newCourseClassEmail
    val fullName = randStr
    val email = randEmail
    val enrollmentRequestsTO = TOs.newEnrollmentRequestsTO(new ArrayList[EnrollmentRequestTO])  
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, "institutionAdmin"+fullName, "institutionAdmin"+email, null, RegistrationType.email, null, false))
    EnrollmentsResource().putEnrollments(enrollmentRequestsTO)
    
    val enrollmentsCreated = EnrollmentsRepo.byCourseClass(courseClass.getUUID)
    assert(enrollmentsCreated.getEnrollmentTOs.size == 1)
    assert({ 
      enrollmentsCreated.getEnrollmentTOs.asScala exists(e => e.getFullName.equals("institutionAdmin"+fullName))
    })
  }  
  
  "The platformAdmin" should 
  "be able to cancel an enrollment" in asPlatformAdmin {
    val fullName = randName
    val email = randEmail
    val courseClass = newCourseClassEmail
    val enrollmentRequestsTO = TOs.newEnrollmentRequestsTO(new ArrayList[EnrollmentRequestTO])
    
    //enroll user
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, fullName, email, null, RegistrationType.email, null, false))
    EnrollmentsResource().putEnrollments(enrollmentRequestsTO)
    
    //check that his enrollment is good
    val enrollmentsCreated = EnrollmentsRepo.byCourseClass(courseClass.getUUID)
    assert(EnrollmentState.enrolled.equals(enrollmentsCreated.getEnrollmentTOs.get(0).getEnrollment.getState))
    
    //remove him from class
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, fullName, email, null, RegistrationType.email, null, true))
    EnrollmentsResource().putEnrollments(enrollmentRequestsTO)
     
    val enrollmentsRemoved = EnrollmentsRepo.byCourseClass(courseClass.getUUID)
    assert(EnrollmentState.cancelled.equals(enrollmentsRemoved.getEnrollmentTOs.get(0).getEnrollment.getState))
  }  
  
    "The institutionAdmin" should 
  "be able to cancel an enrollment" in asInstitutionAdmin {
    val fullName = randName
    val email = randEmail
    val courseClass = newCourseClassEmail
    val enrollmentRequestsTO = TOs.newEnrollmentRequestsTO(new ArrayList[EnrollmentRequestTO])
    
    //enroll user
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, fullName, email, null, RegistrationType.email, null, false))
    EnrollmentsResource().putEnrollments(enrollmentRequestsTO)
    
    //check that his enrollment is good
    val enrollmentsCreated = EnrollmentsRepo.byCourseClass(courseClass.getUUID)
    assert(EnrollmentState.enrolled.equals(enrollmentsCreated.getEnrollmentTOs.get(0).getEnrollment.getState))
    
    //remove him from class
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, fullName, email, null, RegistrationType.email, null, true))
    EnrollmentsResource().putEnrollments(enrollmentRequestsTO)
     
    val enrollmentsRemoved = EnrollmentsRepo.byCourseClass(courseClass.getUUID)
    assert(EnrollmentState.cancelled.equals(enrollmentsRemoved.getEnrollmentTOs.get(0).getEnrollment.getState))
  }  
    
  "The person" should "be able to update his own notes" in asInstitutionAdmin {
    val courseClass = newCourseClassEmail
    val enrollmentRequestsTO = TOs.newEnrollmentRequestsTO(new ArrayList[EnrollmentRequestTO])
    
    //create person manually
    val testPerson = newPerson
    
    //enroll user with values from testPerson
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, testPerson.getFullName, testPerson.getEmail, null, RegistrationType.email, null, false))
    EnrollmentsResource().putEnrollments(enrollmentRequestsTO)
    
    //become testPerson
    asIdentity(testPerson.getUUID) {
    	//update his notes
    	EnrollmentsResource().putNotesChange(courseClass.getUUID, "test notes");
    
    	val enrollmentsFound = EnrollmentsRepo.byCourseClass(courseClass.getUUID);
    	assert("test notes".equals(enrollmentsFound.getEnrollmentTOs.get(0).getEnrollment.getNotes))
    }
  }

  "A user" should "be able to request enrollment in a public class" in asInstitutionAdmin {
    val courseClass = newPublicCourseClass
    val testPerson = newPerson
    
    asIdentity(testPerson.getUUID) {
    	//enroll user
    	val enrollment = Entities.newEnrollment(courseClassUUID = courseClass.getUUID, personUUID = testPerson.getUUID, state = EnrollmentState.requested)
    	EnrollmentsResource().create(enrollment)
    
    	//check that his enrollment is good
    	val enrollmentsCreated = EnrollmentsRepo.byCourseClass(courseClass.getUUID)
    	assert(EnrollmentState.requested.equals(enrollmentsCreated.getEnrollmentTOs.get(0).getEnrollment.getState))
    	assert(testPerson.getUUID.equals(enrollmentsCreated.getEnrollmentTOs.get(0).getPersonUUID))
    }
  }
  
  "A user" should "not be able to request enrollment in a private class" in asInstitutionAdmin {
    val courseClass = newCourseClassEmail
    val testPerson = newPerson
    
    asIdentity(testPerson.getUUID) {
    	//enroll user
    	val enrollment = Entities.newEnrollment(courseClassUUID = courseClass.getUUID, personUUID = testPerson.getUUID, state = EnrollmentState.requested)
    	try {
    		EnrollmentsResource().create(enrollment)
    	} catch {
    	  case ise:IllegalStateException => assert(ise.getCause.eq(RequirementNotMet))
    	  case default:Throwable => fail()
    	}
    }
  }

  "A user" should "not be able to request enrollment for another user" in asInstitutionAdmin {
    val courseClass = newCourseClassEmail
    
    asPerson {
    	//enroll user
    	val enrollment = Entities.newEnrollment(courseClassUUID = courseClass.getUUID, personUUID = person.getUUID(), state = EnrollmentState.requested)
    	try {
    		EnrollmentsResource().create(enrollment)
    	} catch {
    	  //we should throw and catch a more specific exception here so we have something to check
    	  case ise:IllegalStateException => assert(ise.getCause.eq(RequirementNotMet))
    	  case default:Throwable => fail()
    	}
    }
  }
  
  "A user that's not a platform or institutionAdmin" should "not be able to register and enroll one participant" in asInstitutionAdmin {
    val fullName = randName
    val email = randEmail
    val courseClass = newCourseClassEmail
    val enrollmentRequestsTO = TOs.newEnrollmentRequestsTO(new ArrayList[EnrollmentRequestTO])    
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, "notAnAdmin"+fullName, "notAnAdmin"+email, null, RegistrationType.email, null, false))
    
    asPerson {
    	try {
    		EnrollmentsResource().putEnrollments(enrollmentRequestsTO)
    	} catch {
    	  //we should throw and catch a more specific exception here so we have something to check
      		case ise:IllegalStateException => assert(ise.getCause.eq(RequirementNotMet))
      		case default:Throwable => fail()
    	}
    }
  }
}