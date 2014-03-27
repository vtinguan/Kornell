package kornell.server.repository

import java.math.BigDecimal
import java.util.Date
import scala.collection.JavaConverters._
import com.google.web.bindery.autobean.vm.AutoBeanFactorySource
import kornell.core.entity.Course
import kornell.core.entity.Enrollment
import kornell.core.entity.EnrollmentState
import kornell.core.entity.Registration
import kornell.core.to.CourseClassTO
import kornell.core.to.CoursesTO
import kornell.core.to.EnrollmentsTO
import kornell.core.to.RegistrationRequestTO
import kornell.core.to.RegistrationsTO
import kornell.core.to.TOFactory
import kornell.core.util.StringUtils
import kornell.server.repository.s3.S3
import kornell.core.entity.CourseVersion
import kornell.core.to.CourseVersionsTO
import kornell.core.to.EnrollmentRequestTO
import kornell.core.to.EnrollmentRequestsTO
import kornell.core.to.CertificateInformationTO

//TODO: Consider turning to Object
object TOs {
  val tos = AutoBeanFactorySource.create(classOf[TOFactory])

  def newUserInfoTO = tos.newUserInfoTO.as
  def newRegistrationsTO: RegistrationsTO = tos.newRegistrationsTO.as
  def newEnrollmentsTO: EnrollmentsTO = tos.newEnrollmentsTO.as
  def newCoursesTO: CoursesTO = tos.newCoursesTO.as
  def newCourseVersionsTO: CourseVersionsTO = tos.newCourseVersionsTO.as
  
  def newRegistrationsTO(registrationList: List[Registration]): RegistrationsTO = {
    val registrations = newRegistrationsTO
    registrations.setRegistrations(registrationList asJava)
    registrations
  }
  
  def newEnrollmentsTO(enrollmentList: List[Enrollment]): EnrollmentsTO = {
    val enrollments = newEnrollmentsTO
    enrollments.setEnrollments(enrollmentList asJava)
    enrollments
  }
  
  def newCoursesTO(coursesList: List[Course]): CoursesTO = {
    val courses = newCoursesTO
    courses.setCourses(coursesList asJava)
    courses
  }
  
  def newCourseVersionsTO(courseVersionsList: List[CourseVersion]): CourseVersionsTO = {
    val courseVersions = newCourseVersionsTO
    courseVersions.setCourseVersions(courseVersionsList asJava)
    courseVersions
  } 

  def newCourseClassesTO(l: List[CourseClassTO]) = {
    val to = tos.newCourseClassesTO.as
    to.setCourseClasses(l asJava)
    to
  } 
  
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
    distributionPrefix:String,
    //courseClass
    courseClassUUID: String,
    courseClassName: String,
    institutionUUID: String,
    requiredScore:BigDecimal,
    publicClass: Boolean,
    enrollWithCPF: Boolean,
    maxEnrollments: Integer): CourseClassTO = {
      newCourseClassTO(
          courseUUID, 
          code, 
          title, 
          description, 
          infoJson, 
          courseVersionUUID, 
          courseVersionName, 
          repositoryUUID, 
          versionCreatedAt, 
          distributionPrefix, 
          courseClassUUID,
          courseClassName, 
          institutionUUID, 
          requiredScore,
          publicClass,
          enrollWithCPF,
          maxEnrollments,
          null, 
          null, 
          null, 
          null, 
          null, 
          null)
    }
  
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
    distributionPrefix:String,
    //courseClass
    courseClassUUID: String,
    courseClassName: String,
    institutionUUID: String,
    requiredScore: BigDecimal,
    publicClass: Boolean,
    enrollWithCPF: Boolean,
    maxEnrollments: Integer,
    //enrollment
    enrollmentUUID: String, 
    enrolledOn: Date, 
    personUUID: String, 
    progress: String,
    notes: String,
    enrollmentState: String): CourseClassTO = {
	    val classTO = tos.newCourseClassTO.as
	    val versionTO = tos.newCourseVersionTO.as
	    val course = Entities.newCourse(courseUUID, code, title, description, infoJson)
	    val version = Entities.newCourseVersion(courseVersionUUID, courseVersionName, courseUUID, repositoryUUID, versionCreatedAt,distributionPrefix)
	    val clazz = Entities.newCourseClass(courseClassUUID, courseClassName, courseVersionUUID, institutionUUID, requiredScore, publicClass, enrollWithCPF, maxEnrollments)
	    val s3 = S3(version.getRepositoryUUID)
	    versionTO.setDistributionURL(StringUtils.composeURL(s3.baseURL , s3.prefix))
	    versionTO.setCourse(course)
	    versionTO.setCourseVersion(version)
	    classTO.setCourseVersionTO(versionTO)
	    classTO.setCourseClass(clazz)
	    if(enrollmentUUID != null){
		    val prog = if (progress != null) Integer.parseInt(progress) else 0
		    val enrollment = Entities.newEnrollment(enrollmentUUID, enrolledOn, courseClassUUID, personUUID, prog, notes,EnrollmentState.valueOf(enrollmentState))
		    classTO.setEnrollment(enrollment)
	    }
	    classTO
  }
 
  
  def newRegistrationRequestTO:RegistrationRequestTO = tos.newRegistrationRequestTO.as
  def newRegistrationRequestTO(institutionUUID:String,fullName:String,email:String,password:String):RegistrationRequestTO = {
    val to = newRegistrationRequestTO
    to.setInstitutionUUID(institutionUUID)
    to.setFullName(fullName)
    to.setEmail(email)
    to.setPassword(password)
    to
  }
 
  
  def newEnrollmentRequestTO:EnrollmentRequestTO = tos.newEnrollmentRequestTO.as
  def newEnrollmentRequestTO(institutionUUID:String,courseClassUUID:String,fullName: String, email:String,cpf:String):EnrollmentRequestTO = {
    val to = newEnrollmentRequestTO
    to.setInstitutionUUID(institutionUUID)
    to.setCourseClassUUID(courseClassUUID)
    to.setFullName(fullName)
    to.setEmail(email)
    to.setCPF(cpf)
    to
  }
  
  def newEnrollmentRequestsTO:EnrollmentRequestsTO = tos.newEnrollmentRequestsTO.as
  def newEnrollmentRequestsTO(enrollmentRequests: java.util.List[EnrollmentRequestTO]):EnrollmentRequestsTO = {
    val to = newEnrollmentRequestsTO
    to.setEnrollmentRequests(enrollmentRequests)
    to
  }
 
  
  //def newCertificateInformationTO:CertificateInformationTO = tos.newCertificateInformationTO.as
  def newCertificateInformationTO:CertificateInformationTO = new CertificateInformationTO
  def newCertificateInformationTO(personFullName:String,personCPF:String,courseTitle: String, courseClassFinishedDate:Date,assetsURL:String, distributionPrefix:String):CertificateInformationTO = {
    val to = newCertificateInformationTO
    to.setPersonFullName(personFullName)
    to.setPersonCPF(personCPF)
    to.setCourseTitle(courseTitle)
    to.setCourseClassFinishedDate(courseClassFinishedDate)
    to.setAssetsURL(assetsURL)
    to.setDistributionPrefix(distributionPrefix)
    to
  }
}
