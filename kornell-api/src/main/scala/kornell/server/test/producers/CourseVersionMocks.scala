package kornell.server.test.producers

import javax.enterprise.context.Dependent
import javax.inject.Inject
import kornell.core.entity.Institution
import kornell.core.entity.CourseClass
import kornell.server.api.CourseClassesResource
import kornell.core.entity.CourseVersion
import kornell.server.cdi.EmailCourseClass
import javax.enterprise.inject.Produces
import kornell.core.entity.RegistrationEnrollmentType
import kornell.server.jdbc.repository.PeopleRepo
import kornell.core.entity.Person
import kornell.core.entity.Enrollment
import kornell.server.jdbc.repository.EnrollmentRepo
import kornell.server.jdbc.repository.EnrollmentsRepo
import kornell.server.repository.Entities
import kornell.core.entity.EnrollmentState

@Dependent
class CourseVersionMocks @Inject() (
  val enrollsRepo: EnrollmentsRepo,
  val ppl: PeopleRepo,
  val ccr: CourseClassesResource,
  val courseVersion: CourseVersion,
  val itt: Institution) extends Producer {

  def newCourseClassEmail: CourseClass = {
    val cc = ccr.create(
      name = randName,
      courseVersionUUID = courseVersion.getUUID,
      institutionUUID = itt.getUUID,
      registrationEnrollmentType =
        RegistrationEnrollmentType.email)
    logger.info(s"CC = $cc")
    cc
  }

  def newPerson: Person =
    ppl.createPerson(itt.getUUID, randEmail, randName, randCPF)

  def newEnrollment(person: Person, cc: CourseClass): Enrollment =
    enrollsRepo.createEnrollment(personUUID = person.getUUID,
      courseClassUUID = cc.getUUID,
      state = EnrollmentState.requested)

  val student = newPerson
  val courseClassEmail = {
    val ncce = newCourseClassEmail
    if (ncce == null) throw new RuntimeException("WTF?!?")
    ncce
  }
  val enrollment = newEnrollment(student, courseClassEmail)
}