package kornell.server.repository

import com.google.web.bindery.autobean.vm.AutoBeanFactorySource
import kornell.core.shared.to.TOFactory
import kornell.core.shared.data.Institution
import kornell.core.shared.data.Registration
import kornell.core.shared.to.RegistrationsTO
import scala.collection.JavaConverters._
import java.util.Date
import java.math.BigDecimal
import java.sql.ResultSet
import kornell.core.shared.to.CourseTO

//TODO: Consider turning to Object
trait TOs extends Beans {
  val tos = AutoBeanFactorySource.create(classOf[TOFactory])

  def newUserInfoTO = tos.newUserInfoTO.as
  def newRegistrationsTO: RegistrationsTO = tos.newRegistrationsTO.as
  def newRegistrationsTO(registrationsWithInstitutions: Map[Registration, Institution]): RegistrationsTO = {
    val registrations = newRegistrationsTO
    registrations.setRegistrationsWithInstitutions(registrationsWithInstitutions asJava)
    registrations
  }

  def newCourseTO(courseUUID: String, code: String, title: String, description: String, thumbDataURI: String, objectives: String,
    enrollmentUUID: String, enrolledOn: Date, personUUID: String, progress: String) = {
    val to = tos.newCourseTO.as
    val prog = if (progress != null) new BigDecimal(progress) else null
    //TODO: Factory Method x Implicit Conversion 
    to setCourse newCourse(courseUUID, code, title, description, thumbDataURI, objectives)
    to setEnrollment (enrollmentUUID, enrolledOn, courseUUID, personUUID, prog)
    to
  }

  implicit def newCourseTO(r: ResultSet): CourseTO = newCourseTO(
    r.getString("courseUUID"), r.getString("code"), r.getString("title"),
    r.getString("description"), r.getString("assetsURL"), r.getString("infoJson"),
    r.getString("enrollmentUUID"), r.getDate("enrolledOn"),
    r.getString("person_uuid"), r.getString("progress"))

  def newCoursesTO(l: List[CourseTO]) = {
    val to = tos.newCoursesTO.as
    to.setCourses(l asJava)
    to
  }
}