package kornell.server.repository

import com.google.web.bindery.autobean.vm.AutoBeanFactorySource
import kornell.core.to.TOFactory
import kornell.core.entity.Institution
import kornell.core.entity.Registration
import kornell.core.to.RegistrationsTO
import scala.collection.JavaConverters._
import java.util.Date
import java.math.BigDecimal
import java.sql.ResultSet
import kornell.core.to.CourseTO

//TODO: Consider turning to Object
object TOs {
  val tos = AutoBeanFactorySource.create(classOf[TOFactory])

  def newUserInfoTO = tos.newUserInfoTO.as
  def newRegistrationsTO: RegistrationsTO = tos.newRegistrationsTO.as
  def newRegistrationsTO(registrationsWithInstitutions: Map[Registration, Institution]): RegistrationsTO = {
    val registrations = newRegistrationsTO
    registrations.setRegistrationsWithInstitutions(registrationsWithInstitutions asJava)
    registrations
  }

  //TODO: Smells...
  def newCourseTO(
    courseUUID: String, code: String,
    title: String, description: String,
    objectives: String,
    enrollmentUUID: String, enrolledOn: Date, 
    personUUID: String, progress: String,
    repository_uuid:String, notes: String) = {
    val to = tos.newCourseTO.as
    val prog = if (progress != null) new BigDecimal(progress) else null
    val course = Entities.newCourse(courseUUID, code, title, description, objectives, repository_uuid)
    val enrollment = Entities.newEnrollment(enrollmentUUID, enrolledOn, courseUUID, personUUID, prog, notes)
    to setCourse(course)
    to setEnrollment(enrollment)
    to
  }

  implicit def newCourseTO(r: ResultSet): CourseTO = newCourseTO(
    r.getString("courseUUID"), r.getString("code"), r.getString("title"),
    r.getString("description"), r.getString("infoJson"),
    r.getString("enrollmentUUID"), r.getDate("enrolledOn"),
    r.getString("person_uuid"), r.getString("progress"),r.getString("repository_uuid"), r.getString("notes"))

  def newCoursesTO(l: List[CourseTO]) = {
    val to = tos.newCoursesTO.as
    to.setCourses(l asJava)
    to
  }
}