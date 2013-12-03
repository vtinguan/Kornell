package kornell.server.repository.service

import java.util.Date

import scala.collection.JavaConverters.asScalaBufferConverter

import kornell.core.entity.CourseClass
import kornell.core.entity.Enrollment
import kornell.core.entity.EnrollmentState
import kornell.core.entity.Person
import kornell.core.to.UserInfoTO
import kornell.core.util.UUID
import kornell.server.repository.TOs.newUserInfoTO
import kornell.server.repository.jdbc.Auth
import kornell.server.repository.jdbc.CourseClasses
import kornell.server.repository.jdbc.Enrollments
import kornell.server.repository.jdbc.Events
import kornell.server.repository.jdbc.Institutions
import kornell.server.repository.jdbc.People
import kornell.server.repository.jdbc.PersonRepository
import kornell.server.util.EmailSender


object RegistrationEnrollmentService {
  
    def main(args: Array[String]) {
      println(UUID.random)
      println(UUID.random)
      println(UUID.random)
    }

  def deanCreateEnrollmentsBatch(enrollments: kornell.core.entity.Enrollments, dean: Person) =
    enrollments.getEnrollments().asScala.foreach(e => deanCreateEnrollmentBatch(e,dean))

  def deanCreateEnrollmentBatch(enrollment: Enrollment, dean: Person) = {
    val courseClass = CourseClasses(enrollment.getCourseClassUUID()).get
    // create person if it doesn't exist
    Auth.getPerson(enrollment.getPerson.getEmail) match { 
      case Some(one) => deanEnrollExistingPerson(courseClass, one, dean)
      case None => deanEnrollNewPerson(courseClass, enrollment.getPerson.getEmail, enrollment.getPerson.getFullName, dean)
    }
  }

  private def deanEnrollNewPerson(courseClass: CourseClass, email: String, fullName: String, dean: Person) = {
    //TODO: URG: fix date nulls
    val personRepo = People().createPerson(email, fullName, "", "", "", "1800-01-01", "")
    val person = personRepo.get
    personRepo.registerOn(courseClass.getInstitutionUUID)
    Enrollments().createEnrollment(courseClass.getUUID, person.getUUID(), EnrollmentState.preEnrolled)
    //envia email pro cara
    //??? confirmation link
    //sem fullName???
    //EmailSender.sendEmail(user.getPerson(), Institutions.byUUID(institutionUUID).get, confirmationLink)
  }

  private def deanEnrollExistingPerson(courseClass: CourseClass, person: Person, dean: Person) =
    Enrollments().byCourseClassAndPerson(courseClass.getUUID, person.getUUID) match {
      case Some(enrollment) => deanUpdateExistingEnrollment(courseClass.getInstitutionUUID, person, enrollment, dean)
      case None => deanCreateEnrollment(courseClass, person, dean)
    }

  private def deanCreateEnrollment(courseClass: CourseClass, person: Person, dean: Person) = {
    Enrollments().createEnrollment(courseClass.getUUID, person.getUUID, EnrollmentState.preEnrolled)
    //envia email pro cara
    //??? confirmation link
    //sem fullName???
    //EmailSender.sendEmail(user.getPerson(), Institutions.byUUID(institutionUUID).get, confirmationLink)
  }

  private def deanUpdateExistingEnrollment(institutionUUID: String, person: Person, enrollment: Enrollment, dean: Person) = {
    Events.logEnrollmentStateChanged(UUID.random, new Date(), dean.getUUID(), enrollment.getUUID(), enrollment.getState(), EnrollmentState.enrolled)
    //envia email pro cara
    //??? confirmation link
    //sem fullName???
    //EmailSender.sendEmail(user.getPerson(), Institutions.byUUID(institutionUUID).get, confirmationLink)
  }

  def userRequestRegistration(regReq: kornell.core.to.RegistrationRequestTO): UserInfoTO = {
    val email = regReq.getEmail()
    val institutionUUID = regReq.getInstitutionUUID()
    val password = regReq.getPassword()
    val fullName = regReq.getFullName()
    val classes: List[CourseClass] = CourseClasses.byInstitution(institutionUUID)

    Auth.getPerson(email) match {
      case Some(one) => userUpdateExistingPerson(email, fullName, password, classes, one)
      case None => userCreateNewPerson(email, fullName, password, classes, institutionUUID)
    }
  }

  private def userCreateNewPerson(email: String, fullName: String, password: String, classes: List[CourseClass], institutionUUID: String): UserInfoTO = {
    val user = newUserInfoTO
    user.setUsername(email)
    val p: PersonRepository = People().createPerson(email, fullName, "", "", "", "1800-01-01", "")
    p.setPassword(email, password).registerOn(institutionUUID)
    user.setPerson(p.get)
    //if there's only one course, an enrollment must be requested
    if (classes.length == 1)
      Enrollments().createEnrollment(classes.head.getUUID(), p.get.getUUID(), EnrollmentState.requested)
    //EmailSender.sendEmail(user.getPerson(), Institutions.byUUID(institutionUUID).get, confirmationLink)
    user
  }

  private def userUpdateExistingPerson(email: String, fullName: String, password: String, classes: List[CourseClass], personOld: Person): UserInfoTO = {
    val user = newUserInfoTO
    user.setUsername(email)
    val person = PersonRepository(personOld.getUUID()).
      updatePerson(email, fullName, personOld.getCompany(),
        personOld.getTitle(), personOld.getSex(),
        personOld.getBirthDate(), personOld.getConfirmation())

    //update the user's info
    user.setPerson(person.get)
    person.setPassword(email, password)
    //if there's only one course, the enrollment's state should be changed to enrolled
    if (classes.length == 1)
      userUpdateExistingEnrollment(null, user.getPerson())
    //EmailSender.sendEmail(user.getPerson(), Institutions.byUUID(institutionUUID).get, confirmationLink)
    user
  }

  def userUpdateExistingEnrollment(courseClass: CourseClass, person: Person) = {
    val enrollment = Enrollments().byCourseClassAndPerson(courseClass.getUUID, person.getUUID)
    if (enrollment.isDefined && EnrollmentState.preEnrolled.equals(enrollment.get.getState)) {
      Events.logEnrollmentStateChanged(UUID.random, new Date, person.getUUID, enrollment.get.getUUID, enrollment.get.getState, EnrollmentState.enrolled)
    }
    //EmailSender.sendEmail(user.getPerson(), Institutions.byUUID(institutionUUID).get, confirmationLink)
  }
}