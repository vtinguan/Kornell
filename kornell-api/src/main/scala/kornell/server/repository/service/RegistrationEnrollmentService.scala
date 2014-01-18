package kornell.server.repository.service

import java.util.Date
import scala.collection.JavaConverters.asScalaBufferConverter
import kornell.core.entity.Enrollment
import kornell.core.entity.EnrollmentState
import kornell.core.entity.Person
import kornell.core.to.EnrollmentRequestTO
import kornell.core.to.EnrollmentRequestsTO
import kornell.core.to.UserInfoTO
import kornell.core.util.UUID
import kornell.server.repository.Entities
import kornell.server.repository.TOs.newUserInfoTO
import kornell.server.repository.jdbc.Auth
import kornell.server.repository.jdbc.CourseClasses
import kornell.server.repository.jdbc.Enrollments
import kornell.server.repository.jdbc.Events
import kornell.server.repository.jdbc.People
import kornell.server.repository.jdbc.PersonRepository
import kornell.server.util.EmailService
import kornell.server.repository.jdbc.Institutions
import kornell.core.to.RegistrationRequestTO
import kornell.server.repository.jdbc.Courses
import kornell.core.entity.Institution
import scala.collection.mutable.ListBuffer


object RegistrationEnrollmentService {
  
  private def main(args: Array[String]) {
    println(UUID.random)
    println(UUID.random)
    println(UUID.random)
  }

  def deanRequestEnrollments(enrollmentRequests: EnrollmentRequestsTO, dean: Person) = 
    enrollmentRequests.getEnrollmentRequests.asScala.foreach(e => deanRequestEnrollment(e, dean))

  private def deanRequestEnrollment(enrollmentRequest: EnrollmentRequestTO, dean: Person) = 
    Auth.getPersonByEmail(enrollmentRequest.getEmail) match { 
      case Some(one) => deanEnrollExistingPerson(one, enrollmentRequest, dean)
      case None => deanEnrollNewPerson(enrollmentRequest, dean)
  	}

  private def deanEnrollNewPerson(enrollmentRequest: EnrollmentRequestTO, dean: Person) = {
    val personRepo = People.createPerson(enrollmentRequest.getEmail, enrollmentRequest.getFullName)
    personRepo.registerOn(enrollmentRequest.getInstitutionUUID)
    createEnrollment(personRepo.get.get.getUUID, enrollmentRequest.getCourseClassUUID, EnrollmentState.preEnrolled, dean.getUUID)
  }

  private def deanEnrollExistingPerson(person: Person, enrollmentRequest: EnrollmentRequestTO, dean: Person) =
    Enrollments.byCourseClassAndPerson(enrollmentRequest.getCourseClassUUID, person.getUUID) match {
      case Some(enrollment) => deanUpdateExistingEnrollment(person, enrollment, enrollmentRequest.getInstitutionUUID, dean)
      case None => createEnrollment(person.getUUID, enrollmentRequest.getCourseClassUUID, EnrollmentState.preEnrolled, dean.getUUID)
    }
  
  private def deanUpdateExistingEnrollment(person: Person, enrollment: Enrollment, institutionUUID: String, dean: Person) = {
    if(EnrollmentState.cancelled.equals(enrollment.getState)){
    	Events.logEnrollmentStateChanged(UUID.random, new Date(), dean.getUUID, enrollment.getUUID, enrollment.getState, EnrollmentState.preEnrolled)
	    val course = Courses.byCourseClassUUID(enrollment.getCourseClassUUID).get
	    val institution = Institutions.byUUID(institutionUUID).get
	    EmailService.sendEmailEnrolled(person, institution, course)
    }
    else if(EnrollmentState.requested.equals(enrollment.getState()) || EnrollmentState.denied.equals(enrollment.getState())) {
    	Events.logEnrollmentStateChanged(UUID.random, new Date(), dean.getUUID, enrollment.getUUID, enrollment.getState, EnrollmentState.enrolled)
	    val course = Courses.byCourseClassUUID(enrollment.getCourseClassUUID).get
	    val institution = Institutions.byUUID(institutionUUID).get
	    //EmailService.sendEmailEnrolled(person, institution, course)
    }
  }

  def userRequestRegistration(regReq: RegistrationRequestTO): UserInfoTO = {
    val email = regReq.getEmail
    val institutionUUID = regReq.getInstitutionUUID
    val password = regReq.getPassword
    val fullName = regReq.getFullName

    Auth.getPersonByEmail(email) match {
      case Some(one) => userUpdateExistingPerson(email, fullName, password, one)
      case None => userCreateNewPerson(email, fullName, password, institutionUUID)
    }
  }

  private def userCreateNewPerson(email: String, fullName: String, password: String, institutionUUID: String) = {
    val personRepo = People.createPerson(email, fullName)
    
    val user = newUserInfoTO
    user.setPerson(personRepo.get.get)
    user.setUsername(email)
    personRepo.setPassword(email, password).registerOn(institutionUUID)
    //if there's only one class offered by the institution, request an enrollment
    val classes = CourseClasses.byInstitution(institutionUUID)
    if (classes.length == 1){
    	val person = personRepo.get
    	createEnrollment(person.get.getUUID, classes.head.getUUID, EnrollmentState.requested, person.get.getUUID)
    }
    user
  }

  private def userUpdateExistingPerson(email: String, fullName: String, password: String, personOld: Person) = {
    val personRepo = PersonRepository(personOld.getUUID)

    //update the user's info
    val person = Entities.newPerson
    person.setEmail(email)
    person.setFullName(fullName)
    personRepo.update(person)
    
    val user = newUserInfoTO
    user.setPerson(personRepo.get.get)
    user.setUsername(email)
    
    personRepo.setPassword(email, password)
    //for each existing pre-enrollment of this student, enroll
    Enrollments.byStateAndPerson(EnrollmentState.preEnrolled, personRepo.get.get.getUUID).foreach(
    		enrollment => Events.logEnrollmentStateChanged(
    		    UUID.random, new Date, enrollment.getPerson.getUUID, 
    		    enrollment.getUUID, enrollment.getState, EnrollmentState.enrolled)
        )
    user
  }

  private def createEnrollment(personUUID: String, courseClassUUID: String, enrollmentState: EnrollmentState, enrollerUUID: String) = {
	  Enrollments.createEnrollment(courseClassUUID, personUUID, EnrollmentState.notEnrolled)
      val enrollment = Enrollments.byCourseClassAndPerson(courseClassUUID, personUUID).get
      Events.logEnrollmentStateChanged(
    		    UUID.random, new Date, enrollerUUID, 
    		    enrollment.getUUID, enrollment.getState, enrollmentState)
  }
}