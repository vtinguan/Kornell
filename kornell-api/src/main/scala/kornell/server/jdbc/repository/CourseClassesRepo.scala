package kornell.server.jdbc.repository

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.collection.JavaConverters.bufferAsJavaListConverter
import kornell.core.entity.CourseClass
import kornell.core.entity.Person
import kornell.core.entity.Role
import kornell.core.entity.RoleCategory
import kornell.core.entity.RoleType
import kornell.core.to.CourseClassTO
import kornell.server.jdbc.SQL.SQLHelper
import kornell.server.repository.TOs
import kornell.core.entity.Roles
import kornell.core.util.UUID
import java.util.Date
import kornell.core.entity.CourseClassState

class CourseClassesRepo {
}

object CourseClassesRepo {

  def apply(uuid: String) = CourseClassRepo(uuid)

  def create(courseClass: CourseClass):CourseClass = {
    if (courseClass.getUUID == null)
      courseClass.setUUID(UUID.random)
    sql""" 
    	insert into CourseClass(uuid,name,courseVersion_uuid,institution_uuid,publicClass,requiredScore,enrollWithCPF,maxEnrollments,createdAt,createdBy)
    	values(${courseClass.getUUID},
             ${courseClass.getName},
             ${courseClass.getCourseVersionUUID},
             ${courseClass.getInstitutionUUID},
             ${courseClass.isPublicClass},
             ${courseClass.getRequiredScore},
             ${courseClass.isEnrollWithCPF},
             ${courseClass.getMaxEnrollments},
             ${new Date()},
             ${courseClass.getCreatedBy})
    """.executeUpdate
    courseClass
  }

  def byInstitution(institutionUUID: String) =
    sql"""
    | select * from CourseClass where institution_uuid = $institutionUUID
    | where state <> ${CourseClassState.deleted}
    """.map[CourseClass](toCourseClass)

  private def getAllClassesByInstitution(institutionUUID: String): kornell.core.to.CourseClassesTO = {
    TOs.newCourseClassesTO(
      sql"""
			select     
				c.uuid as courseUUID, 
			    c.code,
			    c.title, 
			    c.description,
			    c.infoJson,
			    cv.uuid as courseVersionUUID,
			    cv.name as courseVersionName,
			    cv.repository_uuid as repositoryUUID, 
			    cv.versionCreatedAt as versionCreatedAt,
		  		cv.distributionPrefix as distributionPrefix,
      		cv.contentSpec as contentSpec,
      		cv.disabled as disabled,
			    cc.uuid as courseClassUUID,
			    cc.name as courseClassName,
			    cc.institution_uuid as institutionUUID,
		  		cc.requiredScore as requiredScore,
		  		cc.publicClass as publicClass,
		  		cc.enrollWithCPF as enrollWithCPF,
		  		cc.maxEnrollments as maxEnrollments,
      		cc.createdAt as createdAt,
      		cc.createdBy as createdBy,
      		cc.state,
      		cc.overrideEnrollments as overrideEnrollments
			from Course c
			join CourseVersion cv on cv.course_uuid = c.uuid
			join CourseClass cc on cc.courseVersion_uuid = cv.uuid
		    and cc.institution_uuid = ${institutionUUID}
      where cc.state <> ${CourseClassState.deleted.toString}
		  order by c.title, cc.name;
		""".map[CourseClassTO](toCourseClassTO))
  }

  def byPersonAndInstitution(personUUID: String, institutionUUID: String) = {
    val courseClassesTO = getAllClassesByInstitution(institutionUUID)
    val classes = courseClassesTO.getCourseClasses().asScala
    //bind enrollment if it exists
    classes.foreach(cc => bindEnrollment(personUUID, cc))
    //only return the valid classes for the user (for example, hide private classes)
    courseClassesTO.setCourseClasses(classes.filter(isValidClass _).asJava)
    courseClassesTO
  }

  def administratedByPersonOnInstitution(person: Person, institutionUUID: String, roles: List[Role]) = {
    val courseClassesTO = getAllClassesByInstitution(institutionUUID)
    val classes = courseClassesTO.getCourseClasses().asScala
    courseClassesTO.setCourseClasses(classes.filter(cc => isCourseClassAdmin(cc.getCourseClass().getUUID(), institutionUUID, roles)).asJava)
    courseClassesTO
  }

  private def isValidClass(cc: CourseClassTO): Boolean = {
    cc.getCourseClass().isPublicClass() || cc.getEnrollment() != null
  }

  private def isPlatformAdmin(roles: List[Role]) = {
    var hasRole: Boolean = false
    roles.foreach(role => hasRole = hasRole
      || RoleCategory.isValidRole(role, RoleType.platformAdmin, null, null))
    hasRole
  }

  private def isInstitutionAdmin(institutionUUID: String, roles: List[Role]) = {
    var hasRole: Boolean = isPlatformAdmin(roles)
    roles.foreach(role => hasRole = hasRole
      || RoleCategory.isValidRole(role, RoleType.institutionAdmin, institutionUUID, null))
    hasRole
  }

  private def isCourseClassAdmin(courseClassUUID: String, institutionUUID: String, roles: List[Role]) = {
    var hasRole: Boolean = isInstitutionAdmin(institutionUUID, roles)
    roles.foreach(role => hasRole = hasRole
      || RoleCategory.isValidRole(role, RoleType.courseClassAdmin, null, courseClassUUID))
    hasRole
  }

  private def bindEnrollment(personUUID: String, courseClassTO: CourseClassTO) = {
    val enrollment = EnrollmentsRepo.byCourseClassAndPerson(courseClassTO.getCourseClass().getUUID(), personUUID)
    enrollment foreach courseClassTO.setEnrollment    
  }

}
