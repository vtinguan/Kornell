package kornell.server.repository

import java.math.BigDecimal
import java.util.Date
import java.util.UUID
import scala.collection.JavaConverters.seqAsJavaListConverter
import com.google.web.bindery.autobean.vm.AutoBeanFactorySource
import kornell.core.entity.Course
import kornell.core.entity.Enrollment
import kornell.core.entity.EnrollmentState
import kornell.core.entity.EntityFactory
import kornell.core.entity.Institution
import kornell.core.entity.Person
import kornell.core.entity.Registration
import kornell.core.entity.RoleType
import kornell.server.jdbc.repository.PersonRepo
import java.util.Map
import kornell.core.entity.CourseVersion
import kornell.core.entity.Role
import java.text.SimpleDateFormat
import kornell.server.util.ValueFactory
import kornell.core.util.TimeUtil
import kornell.core.entity.ContentSpec
import kornell.core.entity.Assessment

object Entities {
  val factory = AutoBeanFactorySource.create(classOf[EntityFactory])

  def randUUID = UUID.randomUUID.toString

  def newPerson: Person = factory.newPerson.as

  def newPerson(uuid: String, fullName: String, lastPlaceVisited: String = null,
    email: String, company: String, title: String, sex: String,
    birthDate: Date, confirmation: String, telephone: String, country: String,
    state: String, city: String, addressLine1: String, addressLine2: String,
    postalCode: String, cpf: String) = {

    val bday = ValueFactory.newDate
    val person = factory.newPerson.as
    person.setUUID(uuid)
    person.setFullName(fullName)
    person.setLastPlaceVisited(lastPlaceVisited)
    person.setEmail(email)
    person.setCompany(company)
    person.setTitle(title)
    person.setSex(sex)
    person.setBirthDate(TimeUtil.fromJUD(bday, birthDate))
    person.setConfirmation(confirmation)
    person.setTelephone(telephone)
    person.setCountry(country)
    person.setState(state)
    person.setCity(city)
    person.setAddressLine1(addressLine1)
    person.setAddressLine2(addressLine2)
    person.setPostalCode(postalCode)
    person.setCPF(cpf)
    person
  }

  def newPeople(people: List[Person]) = {
    val ps = factory.newPeople.as
    ps.setPeople(people.asJava)
    ps
  }

  def newPrincipal(uuid: String, personUUID: String, username: String) = {
    val principal = factory.newPrincipal.as
    principal.setUUID(uuid)
    principal.setPersonUUID(personUUID)
    principal.setUsername(username)
    principal
  }

  def newCourse: Course = factory.newCourse.as

  def newCourse(uuid: String, code: String,
    title: String, description: String,
    infoJson: String): Course = {
    val c = newCourse
    c.setUUID(uuid)
    c.setCode(code)
    c.setDescription(description)
    c.setTitle(title)
    c.setInfoJson(infoJson)
    c
  }

  implicit def toEnrollments(enrollments: List[Enrollment]) = {
    val es = factory.newEnrollments.as
    es.setEnrollments(enrollments.asJava)
    es
  }

  def newRoles(roles: List[Role]) = {
    val rs = factory.newRoles.as
    rs.setRoles(roles.asJava)
    rs
  }

  def newEnrollment(uuid: String, enrolledOn: Date,
    courseClassUUID: String, personUUID: String,
    progress: Integer, notes: String,
    state: EnrollmentState, lastProgressUpdate: Date = null,
    assessment: Assessment = null, lastAssessmentUpdate: Date = null,
    assessmentScore:BigDecimal = null, certifiedAt:Date ): Enrollment = {
    val e = factory.enrollment.as
    e.setUUID(uuid)
    e.setEnrolledOn(enrolledOn)
    e.setCourseClassUUID(courseClassUUID)
    e.setPerson(PersonRepo(personUUID).get.get)
    e.setProgress(progress)
    e.setNotes(notes)
    e.setState(state)
    e.setLastProgressUpdate(lastProgressUpdate)
    e.setAssessment(assessment)
    e.setLastAssessmentUpdate(lastAssessmentUpdate)
    e.setAssessmentScore(assessmentScore)
    e.setCertifiedAt(certifiedAt)
    e
  }

  //FTW: Default parameter values
  def newInstitution(uuid: String = randUUID, name: String, fullName: String, terms: String, assetsURL: String, baseURL: String, demandsPersonContactDetails: Boolean, activatedAt: Date) = {
    val i = factory.newInstitution.as
    i.setName(name)
    i.setFullName(fullName)
    i.setUUID(uuid)
    i.setTerms(terms.stripMargin)
    i.setAssetsURL(assetsURL)
    i.setBaseURL(baseURL)
    i.setDemandsPersonContactDetails(demandsPersonContactDetails)
    i.setActivatedAt(activatedAt)
    i
  }

  def newRegistration(p: Person, i: Institution): Registration = {
    val r = newRegistration
    r.setPersonUUID(p.getUUID)
    r.setInstitutionUUID(i.getUUID)
    r
  }

  def newRegistration = factory.newRegistration.as

  def newRegistration(personUUID: String, institutionUUID: String, termsAcceptedOn: Date): Registration = {
    val r = newRegistration
    r.setPersonUUID(personUUID)
    r.setInstitutionUUID(institutionUUID)
    r.setTermsAcceptedOn(termsAcceptedOn)
    r
  }

  def newRegistrations = factory.newRegistrations.as

  lazy val newUserRole = {
    val role = factory.newRole().as
    role.setRoleType(RoleType.user)
    role.setUserRole(factory.newUserRole().as())
    role
  }

  def newPlatformAdminRole(username: String) = {
    val role = factory.newRole().as
    role.setUsername(username)
    role.setRoleType(RoleType.platformAdmin)
    role.setPlatformAdminRole(factory.newPlatformAdminRole().as())
    role
  }

  def newInstitutionAdminRole(username: String, institutionUUID: String) = {
    val role = factory.newRole().as
    role.setUsername(username)
    val institutionAdminRole = factory.newInstitutionAdminRole().as
    institutionAdminRole.setInstitutionUUID(institutionUUID)
    role.setRoleType(RoleType.institutionAdmin)
    role.setInstitutionAdminRole(institutionAdminRole)
    role
  }

  def newCourseClassAdminRole(username: String, courseClassUUID: String) = {
    val role = factory.newRole().as
    role.setUsername(username)
    val courseClassAdminRole = factory.newCourseClassAdminRole().as
    courseClassAdminRole.setCourseClassUUID(courseClassUUID)
    role.setRoleType(RoleType.courseClassAdmin)
    role.setCourseClassAdminRole(courseClassAdminRole)
    role
  }

  def newCourseVersion(uuid: String, name: String, courseUUID: String, repositoryUUID: String, versionCreatedAt: Date = new Date, distributionPrefix: String, contentSpec: String) = {
    val version = factory.newCourseVersion.as
    version.setUUID(uuid);
    version.setName(name);
    version.setCourseUUID(courseUUID);
    version.setRepositoryUUID(repositoryUUID);
    version.setVersionCreatedAt(versionCreatedAt)
    version.setDistributionPrefix(distributionPrefix)
    Option(contentSpec) foreach { spec =>
      val cSpec = ContentSpec.valueOf(spec)
      version.setContentSpec(cSpec);
    }
    version
  }

  def newCourseClass(uuid: String, name: String, courseVersionUUID: String, institutionUUID: String, requiredScore: BigDecimal, publicClass: Boolean, enrollWithCPF: Boolean, maxEnrollments: Integer) = {
    val clazz = factory.newCourseClass.as
    clazz.setUUID(uuid)
    clazz.setName(name)
    clazz.setCourseVersionUUID(courseVersionUUID)
    clazz.setInstitutionUUID(institutionUUID)
    clazz.setRequiredScore(requiredScore)
    clazz.setPublicClass(publicClass)
    clazz.setEnrollWithCPF(enrollWithCPF)
    clazz.setMaxEnrollments(maxEnrollments)
    clazz
  }

  def newWebRepository(uuid: String, distributionURL: String, prefix: String) = {
    val webRepo = factory.newWebReposiory.as
    webRepo.setUUID(uuid)
    webRepo.setPrefix(prefix)
    webRepo.setDistributionURL(distributionURL)
    webRepo
  }

  def newActomEntries(enrollmentUUID: String, actomKey: String, entriesMap: Map[String, String]) = {
    val entries = factory.newActomEntries.as
    entries.setActomKey(actomKey)
    entries.setEnrollmentUUID(enrollmentUUID)
    entries.setEntries(entriesMap)
    entries
  }

}
