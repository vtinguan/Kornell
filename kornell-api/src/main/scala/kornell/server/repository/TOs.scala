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
import kornell.server.repository.s3.S3
import kornell.core.util.StringUtils
import kornell.core.entity.EnrollmentState

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
  
  /*def newCourseClassTO(
    courseUUID: String, code: String,
    title: String, description: String,
    objectives: String,
    enrollmentUUID: String, enrolledOn: Date, 
    personUUID: String, progress: String,
    repository_uuid:String, notes: String) = {
    val to = tos.newCourseClassTO.as
    val prog = if (progress != null) new BigDecimal(progress) else null
    val course = Entities.newCourse(courseUUID, code, title, description, objectives, repository_uuid)
    val enrollment = Entities.newEnrollment(enrollmentUUID, enrolledOn, courseUUID, personUUID, prog, notes,EnrollmentState.notEnrolled)
    to setCourse(course)
    to setEnrollment(enrollment)
    val s3 = S3(to.getCourse.getRepositoryUUID)
    to setDistributionURL( StringUtils.composeURL(s3.baseURL , s3.prefix))
    to
  }*/
  
  def newCourseClassTO(
    //course
    courseUUID: String, 
    code: String,
    title: String, 
    description: String,
    infoJson: String,
    //courseVersion
    courseVersionUUID: String,
    courseVersionName: String,
    repositoryUUID: String, 
    versionCreatedAt: Date,
    //courseClass
    courseClassUUID: String,
    courseClassName: String,
    institutionUUID: String,
    //enrollment
    enrollmentUUID: String, 
    enrolledOn: Date, 
    personUUID: String, 
    progress: String,
    notes: String,
    enrollmentState: String) = {
    val classTO = tos.newCourseClassTO.as
    val versionTO = tos.newCourseVersionTO.as
    val prog = if (progress != null) new BigDecimal(progress) else null
    val course = Entities.newCourse(courseUUID, code, title, description, infoJson)
    val version = Entities.newCourseVersion(courseVersionUUID, courseVersionName, courseUUID, repositoryUUID, versionCreatedAt)
    val clazz = Entities.newCourseClass(courseClassUUID, courseClassName, courseVersionUUID, institutionUUID)
    val enrollment = Entities.newEnrollment(enrollmentUUID, enrolledOn, courseUUID, personUUID, prog, notes,EnrollmentState.valueOf(enrollmentState))
    val s3 = S3(version.getRepositoryUUID)
    versionTO.setDistributionURL(StringUtils.composeURL(s3.baseURL , s3.prefix))
    versionTO.setCourse(course)
    versionTO.setCourseVersion(version)
    classTO.setCourseVersionTO(versionTO)
    classTO.setEnrollment(enrollment)
    classTO.setCourseClass(clazz)
    classTO
  }
 

  def newCoursesTO(l: List[CourseTO]) = {
    val to = tos.newCoursesTO.as
    to.setCourses(l asJava)
    to
  } 
  
  def newRegistrationRequestTO = tos.newRegistrationRequestTO.as
}