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
 
//TODO: Smells bad... but relates to 1-1 to BeanFactory
//TODO: Consider turning to Object
//TODO: Favor composition over inheritance
//TODO: Entities would be a better name
trait Beans {
  val factory = AutoBeanFactorySource.create(classOf[BeanFactory])

  def randomUUID = UUID.randomUUID.toString

  def newPerson(uuid: String, fullName: String,lastPlaceVisited:String = null) = {
    val person = factory.newPerson.as
    person.setUUID(uuid)
    person.setFullName(fullName)
    person.setLastPlaceVisited(lastPlaceVisited)
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

  def newCourse(uuid: String, code: String, title: String, description: String, assetsURL: String, infoJson: String): Course = {
    val c = newCourse
    c.setUUID(uuid)
    c.setCode(code)
    c.setDescription(description)
    c.setAssetsURL(assetsURL)
    c.setTitle(title)
    c.setInfoJson(infoJson)
    c
  }

  /*TODO: Could use a implicit conversion from tuple instead of factory method */
  implicit def toEnrollment(t: Tuple5[String, Date, String, String, BigDecimal]): Enrollment = {
    val e = factory.newEnrollment.as
    e.setUUID(t._1)
    e.setEnrolledOn(t._2)
    e.setCourseUUID(t._3)
    e.setPersonUUID(t._4)
    e.setProgress(t._5)
    e
  }

  //FTW: Default parameter values
  def newInstitution(uuid: String = randomUUID, name: String, terms: String) = {
    val i = factory.newInstitution().as
    i.setName(name)
    i.setUUID(uuid)
    i.setTerms(terms.stripMargin)
    i
  }

  def Registration(p:Person,i:Institution) ={
    val r = newRegistration
    r.setPersonUUID(p.getUUID)
    r.setInstitutionUUID(i.getUUID)
    r
  }
  
  def newRegistration = factory.newRegistration().as
  
  def newRegistration(personUUID:String, institutionUUID:String, termsAcceptedOn:Date):Registration = {
    val r = newRegistration
    r.setPersonUUID(personUUID)
    r.setInstitutionUUID(institutionUUID)
    r.setTermsAcceptedon(termsAcceptedOn)
    r
  }
  
  def newRegistrations = factory.newRegistrations.as

}