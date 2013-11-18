package kornell.server.repository

import java.math.BigDecimal
import java.sql.ResultSet
import java.util.Date
import java.util.UUID
import scala.collection.JavaConverters._
import com.google.web.bindery.autobean.vm.AutoBeanFactorySource
import java.util.ArrayList
import kornell.core.util.StringUtils._
import kornell.core.entity.Institution
import kornell.core.entity.Person
import kornell.core.entity.Course
import kornell.core.entity.Enrollment
import kornell.core.lom.ExternalPage
import kornell.core.lom.Content
import kornell.core.lom.Topic
import kornell.core.entity.Registration
import kornell.core.entity.EntityFactory

object Entities {
  val factory = AutoBeanFactorySource.create(classOf[EntityFactory])

  def randomUUID = UUID.randomUUID.toString

  def newPerson(uuid: String, fullName: String, lastPlaceVisited: String = null,
      	email: String, firstName: String, lastName: String, 
      	company: String, title: String, sex: String, 
      	birthDate: Date, confirmation: String) = {
    val person = factory.newPerson.as
    person.setUUID(uuid)
    person.setFullName(fullName)
    person.setLastPlaceVisited(lastPlaceVisited)
	person.setEmail(email)
    person.setFirstName(firstName)
    person.setLastName(lastName)
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
    assetsURL: String, infoJson: String, repository_uuid: String): Course = {
    val c = newCourse
    c.setUUID(uuid)
    c.setCode(code)
    c.setDescription(description)
    c.setTitle(title)
    c.setInfoJson(infoJson)
    c.setRepositoryUUID(repository_uuid)
    c
  }

  def newEnrollment(t: Tuple6[String, Date, String, String, BigDecimal, String]): Enrollment = {
    val e = factory.newEnrollment.as
    e.setUUID(t._1)
    e.setEnrolledOn(t._2)
    e.setCourseUUID(t._3)
    e.setPersonUUID(t._4)
    e.setProgress(t._5)
    e.setNotes(t._6)
    e
  }

  //FTW: Default parameter values
  def newInstitution(uuid: String = randomUUID, name: String, terms: String, assetsURL: String) = {
    val i = factory.newInstitution.as
    i.setName(name)
    i.setUUID(uuid)
    i.setTerms(terms.stripMargin)
    i.setAssetsURL(assetsURL)
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

  

}