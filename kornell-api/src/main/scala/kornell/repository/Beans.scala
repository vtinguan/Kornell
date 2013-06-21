package kornell.repository

import java.math.BigDecimal
import java.sql.ResultSet
import java.util.Date
import java.util.UUID

import scala.collection.JavaConverters.seqAsJavaListConverter

import com.google.web.bindery.autobean.vm.AutoBeanFactorySource

import kornell.core.shared.data.BeanFactory
import kornell.core.shared.data.Course
import kornell.core.shared.data.CourseTO
import kornell.core.shared.data.Enrollment
import kornell.core.shared.data.Institution

//TODO: Smells bad... but relates to 1-1 to BeanFactory  
trait Beans {
  val factory = AutoBeanFactorySource.create(classOf[BeanFactory])

  def newPerson(uuid: String, fullName: String) = {
    val person = factory.newPerson.as
    person.setUUID(uuid)
    person.setFullName(fullName)
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

  def newCourse(uuid: String, code: String, title: String, description: String, assetsURL: String): Course = {
    val c = newCourse
    c.setUUID(uuid)
    c.setCode(code)
    c.setDescription(description)
    c.setAssetsURL(assetsURL)
    c.setTitle(title)
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

  def newCourseTO(courseUUID: String, code: String, title: String, description: String, thumbDataURI: String,
    enrollmentUUID: String, enrolledOn: Date, personUUID: String, progress: String) = {
    val to = factory.newCourseTO.as
    val prog = if (progress != null) new BigDecimal(progress) else null
    //TODO: Factory Method x Implicit Conversion 
    to setCourse newCourse(courseUUID, code, title, description, thumbDataURI)
    to setEnrollment (enrollmentUUID, enrolledOn, courseUUID, personUUID, prog)
    to
  }

  def newCourseTO(r: ResultSet): CourseTO = newCourseTO(r.getString("courseUUID"), r.getString("code"), r.getString("title"),
    r.getString("description"), r.getString("assetsURL"),
    r.getString("enrollmentUUID"), r.getDate("enrolledOn"),
    r.getString("person_uuid"), r.getString("progress"))

  def newCoursesTO(l: List[CourseTO]) = {
    val to = factory.newCoursesTO.as
    to.setCourses(l asJava)
    to
  }

  //FTW: Default parameter values
  implicit def dehydrate(i: Institution) = List(i.getUUID, i.getName, i.getTerms)

  def Institution(uuid: String = randomUUID, name: String, terms: String) = {
    val to = factory.newInstitution().as
    to.setName(name)
    to.setUUID(uuid)
    to.setTerms(terms.stripMargin)
    to
  }

  def randomUUID = UUID.randomUUID().toString()

}