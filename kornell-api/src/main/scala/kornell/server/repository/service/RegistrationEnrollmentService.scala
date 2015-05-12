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
import kornell.core.util.TimeUtil
import kornell.server.util.ServerTime
import kornell.core.util.StringUtils
import kornell.core.entity.RegistrationType
import kornell.server.jdbc.repository.CourseClassRepo
import kornell.server.jdbc.repository.CourseVersionRepo
import kornell.server.api.ActomResource
import kornell.core.error.exception.EntityConflictException

object RegistrationEnrollmentService {

  def deanRequestEnrollments(enrollmentRequests: EnrollmentRequestsTO, dean: Person) = {
    enrollmentRequests.getEnrollmentRequests.asScala.foreach(e => deanRequestEnrollment(e, dean))
    if (enrollmentRequests.getEnrollmentRequests.size > 100)
      EmailService.sendEmailBatchEnrollment(dean, InstitutionsRepo.byUUID(dean.getInstitutionUUID).get, CourseClassRepo(enrollmentRequests.getEnrollmentRequests.get(0).getCourseClassUUID).get)
  }

  def isInvalidRequestEnrollment(enrollmentRequest: EnrollmentRequestTO, deanUsername: String) = {
    val roles = (Set.empty ++ AuthRepo().rolesOf(deanUsername)).asJava
    !(RoleCategory.isPlatformAdmin(roles) ||
      RoleCategory.isInstitutionAdmin(roles, enrollmentRequest.getInstitutionUUID) ||
      RoleCategory.isCourseClassAdmin(roles, enrollmentRequest.getCourseClassUUID))
  }

  private def deanRequestEnrollment(req: EnrollmentRequestTO, dean: Person) = {
    req.setUsername(req.getUsername.trim)
    PeopleRepo.get(req.getInstitutionUUID, req.getUsername) match {
      case Some(one) => deanEnrollExistingPerson(one, req, dean)
      case None => deanEnrollNewPerson(req, dean)
    }
  }

  private def deanEnrollNewPerson(enrollmentRequest: EnrollmentRequestTO, dean: Person) = {
    val person = enrollmentRequest.getRegistrationType match {
      case RegistrationType.email => PeopleRepo.createPerson(enrollmentRequest.getInstitutionUUID, enrollmentRequest.getUsername, enrollmentRequest.getFullName)
      case RegistrationType.cpf => PeopleRepo.createPersonCPF(enrollmentRequest.getInstitutionUUID, enrollmentRequest.getUsername, enrollmentRequest.getFullName)
      case RegistrationType.username => PeopleRepo.createPersonUsername(enrollmentRequest.getInstitutionUUID, enrollmentRequest.getUsername, enrollmentRequest.getFullName, enrollmentRequest.getInstitutionRegistrationPrefixUUID)
    }
    val personRepo = PersonRepo(person.getUUID)
    if (!enrollmentRequest.getRegistrationType.equals(RegistrationType.email)) {
      personRepo.setPassword(enrollmentRequest.getInstitutionUUID, enrollmentRequest.getUsername, enrollmentRequest.getPassword)
    }
    val enrollment = createEnrollment(personRepo.get.getUUID, enrollmentRequest.getCourseClassUUID, null, EnrollmentState.enrolled, dean.getUUID)
    if (enrollmentRequest.getCourseVersionUUID != null) {
      createChildEnrollments(enrollment, enrollmentRequest.getCourseVersionUUID, person.getUUID, dean.getUUID)
    }
  }

  private def deanEnrollExistingPerson(person: Person, enrollmentRequest: EnrollmentRequestTO, dean: Person) = {
    val personRepo = PersonRepo(person.getUUID)
    if (enrollmentRequest.getCourseVersionUUID == null) {
      EnrollmentsRepo.byCourseClassAndPerson(enrollmentRequest.getCourseClassUUID, person.getUUID) match {
        case Some(enrollment) => deanUpdateExistingEnrollment(person, enrollment, enrollmentRequest.getInstitutionUUID, dean, enrollmentRequest.isCancelEnrollment)
        case None => {
          createEnrollment(person.getUUID, enrollmentRequest.getCourseClassUUID, null, EnrollmentState.enrolled, dean.getUUID)
        }
      }
    } else {
      val courseVersion = CourseVersionRepo(enrollmentRequest.getCourseVersionUUID).get
      if (courseVersion.getParentVersionUUID != null) {
        throw new EntityConflictException("cannotEnrollOnChildVersion")
      }
      EnrollmentsRepo.byCourseClassAndPerson(enrollmentRequest.getCourseClassUUID, person.getUUID) match {
        case Some(enrollment) => deanUpdateExistingEnrollment(person, enrollment, enrollmentRequest.getInstitutionUUID, dean, enrollmentRequest.isCancelEnrollment)
        case None => {
          val enrollment = createEnrollment(person.getUUID, enrollmentRequest.getCourseClassUUID, null, EnrollmentState.enrolled, dean.getUUID)
          createChildEnrollments(enrollment, enrollmentRequest.getCourseVersionUUID, person.getUUID, dean.getUUID)
        }
      }
    }
    //if there's no username set, get it from the enrollment request
    if (StringUtils.isNone(person.getFullName)) {
      person.setFullName(enrollmentRequest.getFullName)
      personRepo.update(person)
    }
  }

  val SEP = ":"
  private def createChildEnrollments(enrollment: Enrollment, courseVersionUUID: String, personUUID: String, deanUUID: String) = {
    val dashboardEnrollmentMap = collection.mutable.Map[String, String]()
    var moduleCounter = 0
    val childEnrollmentMap = Map("knl.dashboard.enrollmentUUID" -> enrollment.getUUID).asJava
    //TODO: julio asks: is this recoverable from the enrollment afterwards without checking ActomEntries?
    CourseVersionRepo(courseVersionUUID).getChildren.foreach(cv => {
      for (i <- 0 until cv.getInstanceCount) {
        val childEnrollment = createEnrollment(personUUID, null, cv.getUUID, EnrollmentState.enrolled, deanUUID)
        dashboardEnrollmentMap("knl.module." + moduleCounter + ".name") = cv.getLabel + SEP + i
        dashboardEnrollmentMap("knl.module." + moduleCounter + ".index") = s"$i"
        dashboardEnrollmentMap("knl.module." + moduleCounter + ".label") = cv.getLabel
        dashboardEnrollmentMap("knl.module." + moduleCounter + ".enrollmentUUID") = childEnrollment.getUUID
        moduleCounter += 1
        //TODO: Reference SCO ID (actomKey) instead of file name
        val childActomResource = new ActomResource(childEnrollment.getUUID, "index.html")
        childActomResource.putEntries(Entities.newActomEntries(childEnrollment.getUUID, "index.html", childEnrollmentMap))
      }
    })
    dashboardEnrollmentMap("knl.module._count") = moduleCounter.toString
    val actomResource = new ActomResource(enrollment.getUUID, "index.html")
    actomResource.putEntries(Entities.newActomEntries(enrollment.getUUID, "index.html", dashboardEnrollmentMap.asJava))
  }

  private def deanUpdateExistingEnrollment(person: Person, enrollment: Enrollment, institutionUUID: String, dean: Person, cancelEnrollment: Boolean) = {
    if (cancelEnrollment && !EnrollmentState.cancelled.equals(enrollment.getState))
      EventsRepo.logEnrollmentStateChanged(UUID.random, ServerTime.now, dean.getUUID, enrollment.getUUID, enrollment.getState, EnrollmentState.cancelled, enrollment.getCourseVersionUUID == null)
    else if (EnrollmentState.cancelled.equals(enrollment.getState)
      || EnrollmentState.requested.equals(enrollment.getState())
      || EnrollmentState.denied.equals(enrollment.getState())) {
      EventsRepo.logEnrollmentStateChanged(UUID.random, ServerTime.now, dean.getUUID, enrollment.getUUID, enrollment.getState, EnrollmentState.enrolled, enrollment.getCourseVersionUUID == null)
    }
  }

  def userRequestRegistration(regReq: RegistrationRequestTO): UserInfoTO = {
    val email = regReq.getEmail
    val username = regReq.getUsername
    val cpf = regReq.getCPF

    PeopleRepo.get(regReq.getInstitutionUUID, username, cpf, email) match {
      case Some(one) => userUpdateExistingPerson(regReq, one)
      case None => userCreateNewPerson(regReq)
    }
  }

  private def userCreateNewPerson(regReq: RegistrationRequestTO) = {
    val person = PeopleRepo.createPerson(regReq.getInstitutionUUID, regReq.getEmail(), regReq.getFullName(), regReq.getCPF())

    val user = newUserInfoTO
    val username = usernameOf(regReq)
    user.setPerson(person)
    user.setUsername(username)
    PersonRepo(person.getUUID).setPassword(regReq.getInstitutionUUID, username, regReq.getPassword)
    user
  }

  def usernameOf(regReq: RegistrationRequestTO) = StringUtils.opt(regReq.getUsername())
    .orElse(regReq.getCPF)
    .orElse(regReq.getEmail)
    .getOrNull

  private def userUpdateExistingPerson(regReq: RegistrationRequestTO, personOld: Person) = {
    val personRepo = PersonRepo(personOld.getUUID)

    //update the user's info
    val person = personRepo.get
    person.setFullName(regReq.getFullName)
    if (regReq.getEmail != null)
      person.setEmail(regReq.getEmail)
    if (regReq.getCPF != null)
      person.setCPF(regReq.getCPF)
    person.setRegistrationType(regReq.getRegistrationType)
    personRepo.update(person)

    val username = usernameOf(regReq)

    val user = newUserInfoTO
    user.setPerson(person)
    user.setUsername(username)

    personRepo.setPassword(regReq.getInstitutionUUID, username, regReq.getPassword)

    user
  }

  private def createEnrollment(personUUID: String, courseClassUUID: String, courseVersionUUID: String, enrollmentState: EnrollmentState, enrollerUUID: String) = {
    val enrollment = EnrollmentsRepo.create(Entities.newEnrollment(null, null, courseClassUUID, personUUID, null, "", EnrollmentState.notEnrolled, null, null, null, null, null, courseVersionUUID))
    EventsRepo.logEnrollmentStateChanged(
      UUID.random, ServerTime.now, enrollerUUID,
      enrollment.getUUID, enrollment.getState, enrollmentState, courseVersionUUID == null)
    enrollment
  }
}
