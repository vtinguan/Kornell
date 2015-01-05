package kornell.server.test

import javax.annotation.PostConstruct
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.ws.rs.Path
import kornell.core.entity.BillingType
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
import kornell.core.entity.ContentSpec
import kornell.core.entity.ContentStore
import kornell.server.jdbc.repository.ContentStoreRepo
import kornell.server.repository.Entities

class Mocks @Inject() (
  val authRepo: AuthRepo,
  val ittsRepo: InstitutionsRepo,
  val pplRepo: PeopleRepo,
  val csRepo: CoursesRepo,
  val cvsRepo: CourseVersionsRepo,
  val enrollsRepo: EnrollmentsRepo,
  val ccRes: CourseClassesResource,
  val cStoreRepo:ContentStoreRepo)
  extends Generator
  with PrivilegeEscalation {

  var platfAdm: Person = _
  var ittAdm: Person = _
  var course: Course = _
  var student: Person = _
  var courseVersion: CourseVersion = _
  var courseClass: CourseClass = _
  var enrollment: Enrollment = _
  var itt: Institution = _
  var cStore:ContentStore = _
  

  @PostConstruct def init = {
    itt = ittsRepo.create(uuid = randUUID,
      name = randName,
      baseURL = randURL,
      billingType = BillingType.enrollment)

    platfAdm = newPerson
    authRepo.grantPlatformAdmin(platfAdm.getUUID)

    ittAdm = {
      val p = pplRepo.createPerson(itt.getUUID, randEmail, randName, randCPF)
      authRepo.grantInstitutionAdmin(p.getUUID, itt.getUUID)
      p
    }

    runAs(ittAdm) {

      course = csRepo.create(uuid = randUUID,
        code = randStr(5),
        institutionUUID = itt.getUUID)
        
     cStore = Entities.newContentStore(randUUID)

      courseVersion = cvsRepo.create(
          repositoryUUID = randUUID,
          courseUUID = course.getUUID,
          contentSpec = ContentSpec.SCORM12.toString)

      courseClass = newCourseClassEmail

      student = newPerson
      enrollment = requestEnrollment(student, courseClass)
    }
  }
  
  def newCourseClassEmail = ccRes.create(
        name = randStr(50),
        courseVersionUUID = courseVersion.getUUID,
        institutionUUID = itt.getUUID,
        registrationEnrollmentType =
          RegistrationEnrollmentType.email)
          
  def newCourseClassCPF = ccRes.create(
        name = randStr(50),
        courseVersionUUID = courseVersion.getUUID,
        institutionUUID = itt.getUUID,
        registrationEnrollmentType =
          RegistrationEnrollmentType.cpf)          
  

  def newPerson: Person =
    pplRepo.createPerson(itt.getUUID, randEmail, randName, randCPF)

  def requestEnrollment(person: Person, cc: CourseClass): Enrollment =
    enrollsRepo.createEnrollment(personUUID = person.getUUID, courseClassUUID = cc.getUUID, state = EnrollmentState.requested)

}