package kornell.server.test.api

import scala.collection.JavaConverters._
import org.junit.runner.RunWith
import org.jboss.arquillian.junit.Arquillian
import kornell.server.test.KornellSuite
import kornell.server.api.EnrollmentsResource
import kornell.server.jdbc.repository.EnrollmentsRepo
import javax.inject.Inject
import org.junit.Test
import kornell.server.test.Mocks
import kornell.server.repository.TOs
import java.util.ArrayList
import kornell.core.to.EnrollmentRequestTO
import kornell.core.entity.RegistrationEnrollmentType
import kornell.core.entity.EnrollmentState
import kornell.core.entity.Person
import kornell.core.entity.CourseClass
import kornell.server.repository.Entities
import kornell.server.util.RequirementNotMet
import kornell.core.to.EnrollmentRequestsTO

@RunWith(classOf[Arquillian])
class EnrollmentsSuite extends KornellSuite {
  @Inject var er: EnrollmentsResource = _
  @Inject var enrollmentsRepo: EnrollmentsRepo = _
  @Inject var mocks: Mocks = _

  @Test def platformAdminCanRegisterAndEnrollParticipantsWithEmail = runAs(mocks.platfAdm) {
    val fullName = randName
    val email = randEmail
    val courseClass = mocks.newCourseClassEmail
    val enrollmentRequestsTO = TOs.newEnrollmentRequestsTO(new ArrayList[EnrollmentRequestTO])
    val totalEnrollments = 10
    for (i <- 1 to totalEnrollments) {
      enrollmentRequestsTO.getEnrollmentRequests.add(
        TOs.newEnrollmentRequestTO(mocks.itt.getUUID,
          courseClass.getUUID,
          i + fullName,
          i + email,
          null,
          RegistrationEnrollmentType.email,
          false))
    }

    er.putEnrollments(enrollmentRequestsTO)

    val enrollmentsCreated = enrollmentsRepo.byCourseClass(courseClass.getUUID)
    assert(enrollmentsCreated.getEnrollmentTOs.size == totalEnrollments)
  }

  @Test def platformAdminCanRegisterAndEnrollParticipantsByCpf = runAs(mocks.platfAdm) {
    val fullName = randStr
    val cpf = randCPF
    val courseClass = mocks.newCourseClassCPF
    val enrollmentRequestsTO = TOs.newEnrollmentRequestsTO(new ArrayList[EnrollmentRequestTO])
    val totalEnrollments = 10
    for (i <- 1 to totalEnrollments) {
      enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(mocks.itt.getUUID,
        courseClass.getUUID, i + fullName, i + cpf, "hunter2", RegistrationEnrollmentType.cpf, false))
    }
    er.putEnrollments(enrollmentRequestsTO)

    val enrollmentsCreated = enrollmentsRepo.byCourseClass(courseClass.getUUID)
    assert(enrollmentsCreated.getEnrollmentTOs.size == totalEnrollments)
  }

  @Test def platformAdminCanNotRegisterParticipantsWithDuplicateEmailsOrCpfs = runAs(mocks.platfAdm) {
    val institution = mocks.itt
    val enrollmentRequestsTO = TOs.newEnrollmentRequestsTO(new ArrayList[EnrollmentRequestTO])
    val courseClassEmail = mocks.newCourseClassEmail
    val courseClassCpf = mocks.newCourseClassCPF
    val fullName = randStr
    val email = randEmail
    val cpf = randCPF
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClassEmail.getUUID, fullName, email, null, RegistrationEnrollmentType.email, false))
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClassCpf.getUUID, fullName, cpf, "hunter2", RegistrationEnrollmentType.cpf, false))
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClassEmail.getUUID, fullName, email, null, RegistrationEnrollmentType.email, false))
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClassCpf.getUUID, fullName, cpf, "hunter2", RegistrationEnrollmentType.cpf, false))
    er.putEnrollments(enrollmentRequestsTO)

    val enrollmentsCreatedEmail = enrollmentsRepo.byCourseClass(courseClassEmail.getUUID)
    assert(enrollmentsCreatedEmail.getEnrollmentTOs.size == 1)
    val enrollmentsCreatedCpf = enrollmentsRepo.byCourseClass(courseClassCpf.getUUID)
    assert(enrollmentsCreatedCpf.getEnrollmentTOs.size == 1)
  }

  @Test def institutionAdminCanRegisterAndEnrollParticipantByEmail = runAs(mocks.ittAdm) {
    val courseClass = mocks.newCourseClassEmail
    val fullName = randStr
    val email = randEmail
    val enrollmentRequestsTO = TOs.newEnrollmentRequestsTO(new ArrayList[EnrollmentRequestTO])
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(mocks.itt.getUUID, courseClass.getUUID, "institutionAdmin" + fullName, "institutionAdmin" + email, null, RegistrationEnrollmentType.email, false))
    er.putEnrollments(enrollmentRequestsTO)

    val enrollmentsCreated = enrollmentsRepo.byCourseClass(courseClass.getUUID)
    assert(enrollmentsCreated.getEnrollmentTOs.size == 1)
    assert({
      enrollmentsCreated.getEnrollmentTOs.asScala exists (e => e.getFullName.equals("institutionAdmin" + fullName))
    })
  }

  //TODO: Review
  /*
 @Test def platformAdminCanCancelEnrollment = runAs(mocks.platfAdm) {
    val fullName = randName
    val email = randEmail
    val courseClass = mocks.newCourseClassEmail
    val enrollmentRequestsTO = TOs.newEnrollmentRequestsTO(new ArrayList[EnrollmentRequestTO])
    
    //enroll user
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(mocks.ittAdm.getUUID, courseClass.getUUID, fullName, email, null, RegistrationEnrollmentType.email, false))
    er.putEnrollments(enrollmentRequestsTO)
    
    //check that his enrollment is good
    val enrollmentsCreated = enrollmentsRepo.byCourseClass(courseClass.getUUID)
    assert(EnrollmentState.enrolled.equals(enrollmentsCreated.getEnrollmentTOs.get(0).getEnrollment.getState))
    
    //remove him from class
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(mocks.itt.getUUID, courseClass.getUUID, fullName, email, null, RegistrationEnrollmentType.email, true))
    er.putEnrollments(enrollmentRequestsTO)
     
    val enrollmentsRemoved = enrollmentsRepo.byCourseClass(courseClass.getUUID)
    assert(EnrollmentState.cancelled.equals(enrollmentsRemoved.getEnrollmentTOs.get(0).getEnrollment.getState))
  }
  */

  @Test def institutionAdminCanCancelEnrollment = runAs(mocks.ittAdm) {
    val fullName = randName
    val email = randEmail
    val courseClass = mocks.newCourseClassEmail
    val enrollmentRequestsTO = TOs.newEnrollmentRequestsTO(new ArrayList[EnrollmentRequestTO])

    //enroll user
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(mocks.itt.getUUID, courseClass.getUUID, fullName, email, null, RegistrationEnrollmentType.email, false))
    er.putEnrollments(enrollmentRequestsTO)

    //check that his enrollment is good
    val enrollmentsCreated = enrollmentsRepo.byCourseClass(courseClass.getUUID)
    assert(EnrollmentState.enrolled.equals(enrollmentsCreated.getEnrollmentTOs.get(0).getEnrollment.getState))

    //remove him from class
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(mocks.itt.getUUID, courseClass.getUUID, fullName, email, null, RegistrationEnrollmentType.email, true))
    er.putEnrollments(enrollmentRequestsTO)

    val enrollmentsRemoved = enrollmentsRepo.byCourseClass(courseClass.getUUID)
    assert(EnrollmentState.cancelled.equals(enrollmentsRemoved.getEnrollmentTOs.get(0).getEnrollment.getState))
  }

  //TODO: Review
  /*   
  @Test def someoneCanUpdateOwnNotes = {
    var testPerson: Person = null
    var courseClass: CourseClass = null
    runAs(mocks.ittAdm) {
      testPerson = mocks.newPerson
      courseClass = mocks.newCourseClassEmail
      val enrollmentRequestsTO = TOs.newEnrollmentRequestsTO(new ArrayList[EnrollmentRequestTO])

      //create person manually
      //enroll user with values from testPerson    
      enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(mocks.ittAdm.getUUID,
        courseClass.getUUID, testPerson.getFullName, testPerson.getEmail, null,
        RegistrationEnrollmentType.email, false))
      er.putEnrollments(enrollmentRequestsTO)
    }

    //become testPerson
    runAs(testPerson) {
      //update his notes
      er.putNotesChange(courseClass.getUUID, "test notes");

      val enrollmentsFound = enrollmentsRepo.byCourseClass(courseClass.getUUID);
      assert("test notes".equals(enrollmentsFound.getEnrollmentTOs.get(0).getEnrollment.getNotes))
    }
  }
  */

  //TODO: Review
  /*
  @Test def someoneCanRequestEnrollment() = {
    
    var courseClass:CourseClass = null 
    var testPerson:Person = null; 
    
    runAs(mocks.ittAdm) {
      courseClass = mocks.newCourseClassEmail
      testPerson = mocks.newPerson
    }
    
    runAs(testPerson) {
    	//enroll user
    	val enrollment = Entities.newEnrollment(courseClassUUID = courseClass.getUUID, personUUID = testPerson.getUUID, state = EnrollmentState.requested)
    	er.create(enrollment)
    
    	//check that his enrollment is good
    	val enrollmentsCreated = enrollmentsRepo.byCourseClass(courseClass.getUUID)
    	assert(EnrollmentState.requested.equals(enrollmentsCreated.getEnrollmentTOs.get(0).getEnrollment.getState))
    	assert(testPerson.getUUID.equals(enrollmentsCreated.getEnrollmentTOs.get(0).getPersonUUID))
    }
  }
  */

  //TODO: Review
  /*
   @Test def studentCanNotRequestEnrollmentInPrivateClass = {
     
    var courseClass:CourseClass = null 
    var testPerson:Person = null
    
    runAs(mocks.ittAdm) {
      courseClass = mocks.newCourseClassEmail
      testPerson = mocks.newPerson
    }
    
    runAs(testPerson) {
    	//enroll user
    	val enrollment = Entities.newEnrollment(courseClassUUID = courseClass.getUUID, personUUID = testPerson.getUUID, state = EnrollmentState.requested)
    	try {
    		er.create(enrollment)
    	} catch {
    	  case ise:IllegalStateException => assert(ise.getCause.eq(RequirementNotMet))
    	  case default:Throwable => fail()
    	}
    }
  }
  */

  //TODO: Review
  /*
   @Test def studentCanNotRequestEnrollmentForAnotherUser = {
    var courseClass:CourseClass =  null
    
    runAs(mocks.ittAdm){
      courseClass = mocks.newCourseClassEmail
    }
    
    runAs(mocks.student) {
    	//enroll user
    	val enrollment = Entities.newEnrollment(courseClassUUID = courseClass.getUUID,
    	    personUUID = mocks.ittAdm.getUUID, 
    	    state = EnrollmentState.requested)
    	try {
    		er.create(enrollment)
    	} catch {
    	  //we should throw and catch a more specific exception here so we have something to check
    	  case ise:IllegalStateException => assert(ise.getCause.eq(RequirementNotMet))
    	  case default:Throwable => fail()
    	}
    }
  }
  */

  //TODO: Review
  /*
  @Test def someoneCanNotRegisterAndEnrollOthers {
    val fullName = randName
    val email = randEmail
    var enrollmentRequestsTO:EnrollmentRequestsTO = null
    
    runAs(mocks.ittAdm) {
      val courseClass = mocks.newCourseClassEmail
      val enrollmentRequestsTO = TOs.newEnrollmentRequestsTO(new ArrayList[EnrollmentRequestTO])
      enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(mocks.itt.getUUID,
        courseClass.getUUID, randName, randEmail, null, RegistrationEnrollmentType.email, false))
    }
    
    runAs(mocks.student) {
      try {
        er.putEnrollments(enrollmentRequestsTO)
      } catch {
        //we should throw and catch a more specific exception here so we have something to check
        case ise: IllegalStateException => assert(ise.getCause.eq(RequirementNotMet))
        case default: Throwable => fail()
      }
    }
  }
  */

}