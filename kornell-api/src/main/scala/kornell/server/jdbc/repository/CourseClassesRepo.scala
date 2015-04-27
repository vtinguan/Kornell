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
import java.sql.ResultSet
import kornell.core.error.exception.EntityConflictException

class CourseClassesRepo {
}

object CourseClassesRepo {

  def apply(uuid: String) = CourseClassRepo(uuid)

  def create(courseClass: CourseClass):CourseClass = {
    val courseClassExists = sql"""
	    select count(*) from CourseClass where courseVersion_uuid = ${courseClass.getCourseVersionUUID} and name = ${courseClass.getName}
	    """.first[String].get
    if (courseClassExists == "0") {
	    if (courseClass.getUUID == null){
	      courseClass.setUUID(UUID.random)
	    }
	    sql""" 
	    	insert into CourseClass(uuid,name,courseVersion_uuid,institution_uuid,publicClass,requiredScore,overrideEnrollments,invisible,maxEnrollments,createdAt,createdBy,registrationType,institutionRegistrationPrefixUUID)
	    	values(${courseClass.getUUID},
	             ${courseClass.getName},
	             ${courseClass.getCourseVersionUUID},
	             ${courseClass.getInstitutionUUID},
	             ${courseClass.isPublicClass},
	             ${courseClass.getRequiredScore},
	             ${courseClass.isOverrideEnrollments},
	             ${courseClass.isInvisible},
	             ${courseClass.getMaxEnrollments},
	             ${new Date()},
	             ${courseClass.getCreatedBy},
	             ${courseClass.getRegistrationType.toString},
	             ${courseClass.getInstitutionRegistrationPrefixUUID})
	    """.executeUpdate
	    courseClass
    } else {
      throw new EntityConflictException("courseClassAlreadyExists")
    }
  }

  def byInstitution(institutionUUID: String) =
    sql"""
    | select * from CourseClass where institution_uuid = $institutionUUID
    | where state <> ${CourseClassState.deleted}
    """.map[CourseClass](toCourseClass)

  private def getAllClassesByInstitution(institutionUUID: String): kornell.core.to.CourseClassesTO = 
    getAllClassesByInstitutionPaged(institutionUUID, "", Int.MaxValue, 1)
    
  private def getAllClassesByInstitutionPaged(institutionUUID: String, searchTerm: String, pageSize: Int, pageNumber: Int): kornell.core.to.CourseClassesTO = {
    val resultOffset = (pageNumber.max(1) - 1) * pageSize
    val filteredSearchTerm = '%' + Option(searchTerm).getOrElse("") + '%'
    
    val courseClassesTO = TOs.newCourseClassesTO(
      sql"""
			select     
				c.uuid as courseUUID, 
			    c.code,
			    c.title, 
			    c.description,
			    c.infoJson,
      			c.childCourse,
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
      			cc.overrideEnrollments as overrideEnrollments,
      			cc.invisible as invisible,
		  		cc.maxEnrollments as maxEnrollments,
      			cc.createdAt as createdAt,
      			cc.createdBy as createdBy,
      			cc.state,
		  		cc.registrationType as registrationType,
		  		cc.institutionRegistrationPrefixUUID as institutionRegistrationPrefixUUID,
      			irp.name as institutionRegistrationPrefixName
			from Course c
				join CourseVersion cv on cv.course_uuid = c.uuid
				join CourseClass cc on cc.courseVersion_uuid = cv.uuid and cc.institution_uuid = ${institutionUUID}
			    left join InstitutionRegistrationPrefix irp on irp.uuid = cc.institutionRegistrationPrefixUUID
      	  	where cc.state <> ${CourseClassState.deleted.toString} and
            (cv.name like ${filteredSearchTerm}
            or cc.name like ${filteredSearchTerm})
      	  	order by cc.state, c.title, cv.versionCreatedAt desc, cc.name limit ${resultOffset}, ${pageSize};
		""".map[CourseClassTO](toCourseClassTO))
		courseClassesTO.setCount(
		    sql"""select count(cc.uuid) from CourseClass cc where cc.state <> ${CourseClassState.deleted.toString}
		    	and cc.institution_uuid = ${institutionUUID}""".first[String].get.toInt)
    	courseClassesTO.setPageSize(pageSize)
    	courseClassesTO.setPageNumber(pageNumber.max(1))
    	courseClassesTO.setSearchCount({
    	  if (searchTerm == "")
    		  0
		  else
		    sql"""select count(cc.uuid) from CourseClass cc 
		    	join CourseVersion cv on cc.courseVersion_uuid = cv.uuid
		    	where cc.state <> ${CourseClassState.deleted.toString} and
            	(cv.name like ${filteredSearchTerm}
            	or cc.name like ${filteredSearchTerm})
            	and cc.institution_uuid = ${institutionUUID}""".first[String].get.toInt
    	})
		courseClassesTO
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

  def administratedByPersonOnInstitution(personUUID: String, institutionUUID: String, searchTerm: String, pageSize: Int, pageNumber: Int, roles: List[Role]) = {
    val courseClassesTO = getAllClassesByInstitutionPaged(institutionUUID, searchTerm, pageSize, pageNumber)
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

  implicit def toString(rs: ResultSet): String = rs.getString(1)
}
