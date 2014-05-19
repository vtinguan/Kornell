package kornell.server.repository.service

import kornell.server.jdbc.repository.EventsRepo
import kornell.server.repository.Entities._
import kornell.server.repository.TOs._
import kornell.server.jdbc.repository.CourseClassesRepo
import kornell.core.to.RegistrationRequestTO
import kornell.server.jdbc.repository.EnrollmentsRepo
import kornell.server.util.EmailService
import kornell.core.to.EnrollmentRequestsTO
import kornell.core.to.EnrollmentRequestTO
import kornell.core.entity.EnrollmentState
import kornell.core.entity.Person
import kornell.core.entity.Enrollment
import kornell.server.jdbc.repository.CoursesRepo
import kornell.server.jdbc.repository.PeopleRepo
import kornell.server.jdbc.repository.InstitutionsRepo
import kornell.core.to.UserInfoTO
import kornell.server.jdbc.repository.AuthRepo
import kornell.core.util.UUID
import java.util.Date
import kornell.server.jdbc.SQL._
import scala.collection.JavaConverters._
import kornell.server.jdbc.repository.PersonRepo
import kornell.core.entity.RoleCategory
import kornell.server.repository.Entities

object RegistrationEnrollmentService {

  def deanRequestEnrollments(enrollmentRequests: EnrollmentRequestsTO, dean: Person) = 
    	enrollmentRequests.getEnrollmentRequests.asScala.foreach(e => deanRequestEnrollment(e, dean)) 
  

  def isInvalidRequestEnrollment(enrollmentRequest: EnrollmentRequestTO, deanUsername: String) = {
    val roles = (Set.empty ++ AuthRepo.rolesOf(deanUsername)).asJava
    !(RoleCategory.isPlatformAdmin(roles) ||
        RoleCategory.isInstitutionAdmin(roles, enrollmentRequest.getInstitutionUUID))
  }

  private def deanRequestEnrollment(enrollmentRequest: EnrollmentRequestTO, dean: Person) =
    PeopleRepo.getByEmailOrCPF({
      if(enrollmentRequest.getEmail != null)
        enrollmentRequest.getEmail
      else
        enrollmentRequest.getCPF
    }) match {
      case Some(one) => deanEnrollExistingPerson(one, enrollmentRequest, dean)
      case None => deanEnrollNewPerson(enrollmentRequest, dean)
    }

  private def deanEnrollNewPerson(enrollmentRequest: EnrollmentRequestTO, dean: Person) = {
    val person = {
      if(enrollmentRequest.getEmail() != null) {
        PeopleRepo.createPerson(enrollmentRequest.getEmail, enrollmentRequest.getFullName)
      } else {
        PeopleRepo.createPersonCPF(enrollmentRequest.getCPF, enrollmentRequest.getFullName)
      }
    }
    val personRepo = PersonRepo(person.getUUID)
    if(enrollmentRequest.getEmail() == null) {
        personRepo.setPassword(enrollmentRequest.getCPF, enrollmentRequest.getCPF)
    }
    personRepo.registerOn(enrollmentRequest.getInstitutionUUID)
    createEnrollment(personRepo.get.get.getUUID, enrollmentRequest.getCourseClassUUID, EnrollmentState.preEnrolled, dean.getUUID)
  }

  private def deanEnrollExistingPerson(person: Person, enrollmentRequest: EnrollmentRequestTO, dean: Person) =
    EnrollmentsRepo.byCourseClassAndPerson(enrollmentRequest.getCourseClassUUID, person.getUUID) match {
      case Some(enrollment) => deanUpdateExistingEnrollment(person, enrollment, enrollmentRequest.getInstitutionUUID, dean)
      case None => {
    	PersonRepo(person.getUUID).registerOn(enrollmentRequest.getInstitutionUUID)
        createEnrollment(person.getUUID, enrollmentRequest.getCourseClassUUID, EnrollmentState.preEnrolled, dean.getUUID)
      }
    }

  private def deanUpdateExistingEnrollment(person: Person, enrollment: Enrollment, institutionUUID: String, dean: Person) = {
    if (EnrollmentState.cancelled.equals(enrollment.getState) 
        || EnrollmentState.requested.equals(enrollment.getState()) 
        || EnrollmentState.denied.equals(enrollment.getState())) {
      EventsRepo.logEnrollmentStateChanged(UUID.random, new Date(), dean.getUUID, enrollment.getUUID, enrollment.getState, EnrollmentState.enrolled)
      val course = CoursesRepo.byCourseClassUUID(enrollment.getCourseClassUUID).get
      val institution = InstitutionsRepo.byUUID(institutionUUID).get
    }
  }

  def userRequestRegistration(regReq: RegistrationRequestTO): UserInfoTO = {
    val email = regReq.getEmail
    val institutionUUID = regReq.getInstitutionUUID
    val password = regReq.getPassword
    val fullName = regReq.getFullName

    PeopleRepo.getByEmailOrCPF(email) match {
      case Some(one) => userUpdateExistingPerson(email, fullName, password, one)
      case None => userCreateNewPerson(email, fullName, password, institutionUUID)
    }
  }

  private def userCreateNewPerson(email: String, fullName: String, password: String, institutionUUID: String) = {
    val person = PeopleRepo.createPerson(email, fullName)

    val user = newUserInfoTO
    user.setPerson(person)
    user.setUsername(email)
    PersonRepo(person.getUUID).setPassword(email, password).registerOn(institutionUUID)
    user
  }

  private def userUpdateExistingPerson(email: String, fullName: String, password: String, personOld: Person) = {
    val personRepo = PersonRepo(personOld.getUUID)

    //update the user's info
    val person = newPerson
    person.setEmail(email)
    person.setFullName(fullName)
    personRepo.update(person)

    val user = newUserInfoTO
    user.setPerson(personRepo.get.get)
    user.setUsername(email)

    personRepo.setPassword(email, password)
    //for each existing pre-enrollment of this student, enroll
    EnrollmentsRepo.byStateAndPerson(EnrollmentState.preEnrolled, personRepo.get.get.getUUID).foreach(
      enrollment => EventsRepo.logEnrollmentStateChanged(
        UUID.random, new Date, enrollment.getPerson.getUUID,
        enrollment.getUUID, enrollment.getState, EnrollmentState.enrolled))
    user
  }

  private def createEnrollment(personUUID: String, courseClassUUID: String, enrollmentState: EnrollmentState, enrollerUUID: String) = {
    val enrollment = EnrollmentsRepo.create(Entities.newEnrollment(null, null, courseClassUUID, personUUID, null, "", EnrollmentState.notEnrolled,null,null,null))
    EventsRepo.logEnrollmentStateChanged(
      UUID.random, new Date, enrollerUUID,
      enrollment.getUUID, enrollment.getState, enrollmentState)
  }
}