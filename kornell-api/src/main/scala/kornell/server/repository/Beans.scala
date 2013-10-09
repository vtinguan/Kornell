package kornell.server.repository

import java.math.BigDecimal
import java.sql.ResultSet
import java.util.Date
import java.util.UUID
import scala.collection.JavaConverters.seqAsJavaListConverter
import com.google.web.bindery.autobean.vm.AutoBeanFactorySource
import kornell.core.shared.data.BeanFactory
import kornell.core.shared.data.Course
import kornell.core.shared.data.Enrollment
import kornell.core.shared.data.Institution
import kornell.core.shared.data.Person
import kornell.core.shared.data.Registration
import java.util.ArrayList
import kornell.core.shared.data.Content
import kornell.core.shared.data.ContentFormat
import kornell.core.shared.data.ExternalPage
import kornell.core.shared.data.Topic

//TODO: Smells bad... but relates to 1-1 to BeanFactory
//TODO: Consider turning to Object
//TODO: Favor composition over inheritance
//TODO: Entities would be a better name
object Beans {
  val factory = AutoBeanFactorySource.create(classOf[BeanFactory])

  def randomUUID = UUID.randomUUID.toString

  def newPerson(uuid: String, fullName: String, lastPlaceVisited: String = null,
      	email: String, firstName: String, lastName: String, 
      	company: String, title: String, sex: String, 
      	birthDate: Date, usernamePrivate: Boolean, emailPrivate: Boolean, 
      	firstNamePrivate: Boolean, lastNamePrivate: Boolean, companyPrivate: Boolean, 
      	titlePrivate: Boolean, sexPrivate: Boolean, birthDatePrivate: Boolean) = {
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
    person.setUsernamePrivate(usernamePrivate)
    person.setEmailPrivate(emailPrivate)
    person.setFirstNamePrivate(firstNamePrivate)
    person.setLastNamePrivate(lastNamePrivate)
    person.setCompanyPrivate(companyPrivate)
    person.setTitlePrivate(titlePrivate)
    person.setSexPrivate(sexPrivate)
    person.setBirthDatePrivate(birthDatePrivate)
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
    c.setAssetsURL(assetsURL)
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

  def newTopic(name: String = "") = {
    val topic = factory.newTopic.as    
    topic.setName(name)
    topic.setChildren(new ArrayList)
    topic
  }
  
  def newContent(topic:Topic) = {
    val content = factory.newContent.as    
    content.setFormat(ContentFormat.Topic);
    content.setTopic(topic)
    content
  }

  def newExternalPage(key: String = "", title: String = "") = {
    val page = factory.newExternalPage.as
    page.setTitle(title)
    page.setKey(key)
    page    
  }
  
  def newContent(page:ExternalPage) = {
    val content = factory.newContent.as    
    content.setFormat(ContentFormat.ExternalPage);
    content.setExternalPage(page)
    content
  }

  def newContents(children: List[Content] = List()) = {
    val contents = factory.newContents.as
    contents.setChildren(children asJava)
    contents
  }
  
  

}