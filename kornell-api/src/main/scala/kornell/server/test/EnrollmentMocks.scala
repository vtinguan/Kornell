package kornell.server.test

import java.util.logging.Logger
import javax.annotation.PostConstruct
import javax.enterprise.context.Dependent
import javax.inject.Inject
import kornell.core.entity.Course
import kornell.core.entity.CourseClass
import kornell.core.entity.CourseVersion
import kornell.core.entity.Enrollment
import kornell.core.entity.EnrollmentState
import kornell.core.entity.Institution
import kornell.core.entity.Person
import kornell.core.entity.RegistrationEnrollmentType
import kornell.server.api.CourseClassesResource
import kornell.server.jdbc.repository.AuthRepo
import kornell.server.jdbc.repository.CourseVersionsRepo
import kornell.server.jdbc.repository.CoursesRepo
import kornell.server.jdbc.repository.EnrollmentsRepo
import kornell.server.jdbc.repository.InstitutionsRepo
import kornell.server.jdbc.repository.PeopleRepo
import kornell.core.entity.BillingType

@Dependent
class EnrollmentMocks @Inject()
  extends Mock {

  @Inject var csRepo: CoursesRepo = _
  @Inject() var cvsRepo: CourseVersionsRepo = _
  @Inject() var enrollsRepo: EnrollmentsRepo = _
  @Inject() var pplRepo: PeopleRepo = _
  @Inject() var authRepo: AuthRepo = _
  @Inject() var ccRes: CourseClassesResource = _
  @Inject() var ittsRepo: InstitutionsRepo = _

  val logger: Logger = Logger.getLogger(classOf[EnrollmentMocks].getName)
  logger.finest("Instantiated EnrollmentMocks")

  var ittAdm: Person = _
  var course: Course = _
  var student: Person = _
  var courseVersion: CourseVersion = _
  var courseClass: CourseClass = _
  var enrollment: Enrollment = _
  var itt: Institution = _

  @PostConstruct def init = {
    itt = ittsRepo.create(uuid = randUUID,
      name = randName,
      baseURL = randURL,
      billingType = BillingType.enrollment)

    ittAdm = {
      val p = pplRepo.createPerson(itt.getUUID, randEmail, randName, randCPF)
      authRepo.grantInstitutionAdmin(p.getUUID, itt.getUUID)
      p
    }

    runAs(ittAdm) {

      course = csRepo.create(uuid = randUUID,
        code = randStr(5),
        institutionUUID = itt.getUUID)

      courseVersion = cvsRepo.create(repositoryUUID = randUUID,
        courseUUID = course.getUUID)

      courseClass = ccRes.create(
        name = randName,
        courseVersionUUID = courseVersion.getUUID,
        institutionUUID = itt.getUUID,
        registrationEnrollmentType =
          RegistrationEnrollmentType.email)

      student = newPerson
      enrollment = newEnrollment(student, courseClass)
    }
  }

  def newPerson: Person =
    pplRepo.createPerson(itt.getUUID, randEmail, randName, randCPF)

  def newEnrollment(person: Person, cc: CourseClass): Enrollment =
    enrollsRepo.createEnrollment(personUUID = person.getUUID,
      courseClassUUID = cc.getUUID,
      state = EnrollmentState.requested)
}