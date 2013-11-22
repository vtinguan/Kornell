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
import kornell.server.repository.jdbc.PersonRepository

object Entities {
  val factory = AutoBeanFactorySource.create(classOf[EntityFactory])

  def randomUUID = UUID.randomUUID.toString

  def newPerson(uuid: String, fullName: String, lastPlaceVisited: String = null,
      	email: String, company: String, title: String, sex: String, 
      	birthDate: Date, confirmation: String) = {
    val person = factory.newPerson.as
    person.setUUID(uuid)
    person.setFullName(fullName)
    person.setLastPlaceVisited(lastPlaceVisited)
	person.setEmail(email)
    person.setCompany(company)
    person.setTitle(title)
    person.setSex(sex)
    person.setBirthDate(birthDate)
    person.setConfirmation(confirmation)
    person
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
    infoJson: String, repository_uuid: String): Course = {
    val c = newCourse
    c.setUUID(uuid)
    c.setCode(code)
    c.setDescription(description)
    c.setTitle(title)
    c.setInfoJson(infoJson)
    c.setRepositoryUUID(repository_uuid)
    c
  }
  
  def newEnrollments(enrollments: List[Enrollment]) = {
    val es = factory.newEnrollments.as
    es.setEnrollments(enrollments.asJava)
    es
  }

  def newEnrollment(uuid: String, enrolledOn: Date, courseUUID: String, personUUID: String, progress: BigDecimal, notes: String, state: EnrollmentState): Enrollment = {
    val e = factory.newEnrollment.as
    e.setUUID(uuid)
    e.setEnrolledOn(enrolledOn)
    e.setCourseUUID(courseUUID)
    e.setPerson(PersonRepository.apply(personUUID).get().get)
    e.setProgress(progress)
    e.setNotes(notes)
    e.setState(state)
    e
  }

  //FTW: Default parameter values
  def newInstitution(uuid: String = randomUUID, name: String, terms: String, assetsURL: String, baseURL: String) = {
    val i = factory.newInstitution.as
    i.setName(name)
    i.setUUID(uuid)
    i.setTerms(terms.stripMargin)
    i.setAssetsURL(assetsURL)
    i.setBaseURL(baseURL)
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
    r.setTermsAcceptedon(termsAcceptedOn)
    r
  }

  def newRegistrations = factory.newRegistrations.as

  
  lazy val newUserRole = {
    val role = factory.newRole().as
    role.setRoleType(RoleType.user)
    role.setUserRole(factory.newUserRole().as())
    role
  }
  
  def newDeanRole(institutionUUID:String) = {
    val role = factory.newRole().as
    val dean = factory.newDeanRole().as
    dean.setInstitutionUUID(institutionUUID)
    role.setRoleType(RoleType.dean)    
    role.setDeanRole(dean)
    role
  }
  

}