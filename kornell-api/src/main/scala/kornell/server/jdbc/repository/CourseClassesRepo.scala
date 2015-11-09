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
import kornell.core.util.StringUtils
import kornell.core.entity.AuditedEntityType

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
	    	insert into CourseClass(uuid,name,courseVersion_uuid,institution_uuid,publicClass,requiredScore,overrideEnrollments,invisible,maxEnrollments,createdAt,createdBy,registrationType,institutionRegistrationPrefixUUID, courseClassChatEnabled, allowBatchCancellation, tutorChatEnabled,approveEnrollmentsAutomatically)
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
	             ${courseClass.getInstitutionRegistrationPrefixUUID},
	             ${courseClass.isCourseClassChatEnabled},
	             ${courseClass.isAllowBatchCancellation},
	             ${courseClass.isTutorChatEnabled},
	             ${courseClass.isApproveEnrollmentsAutomatically})
	    """.executeUpdate
	    ChatThreadsRepo.addParticipantsToCourseClassThread(courseClass)
	    
	    //log creation event
	    EventsRepo.logEntityChange(courseClass.getInstitutionUUID, AuditedEntityType.courseClass, courseClass.getUUID, null, courseClass)
	    courseClass
    } else {
      throw new EntityConflictException("courseClassAlreadyExists")
    }
  }

  def byInstitution(institutionUUID: String) =
    sql"""
    | select * from CourseClass where institution_uuid = $institutionUUID
    | and state <> ${CourseClassState.deleted}
    """.map[CourseClass](toCourseClass)

  private def getAllClassesByInstitution(institutionUUID: String): kornell.core.to.CourseClassesTO = 
    getAllClassesByInstitutionPaged(institutionUUID, "", Int.MaxValue, 1, "", null, null) 
    
  def getCourseClassTO(institutionUUID: String, courseClassUUID: String) = {
    val courseClassesTO = getAllClassesByInstitutionPaged(institutionUUID, "", Int.MaxValue, 1, "", null, courseClassUUID) 
    if(courseClassesTO.getCourseClasses.size > 0){
      courseClassesTO.getCourseClasses.get(0)
    }
  }
    
  def getAllClassesByInstitutionPaged(institutionUUID: String, searchTerm: String, pageSize: Int, pageNumber: Int, adminUUID: String, courseVersionUUID: String, courseClassUUID: String ): kornell.core.to.CourseClassesTO = {
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
		  		cc.courseClassChatEnabled as courseClassChatEnabled, 
		  		cc.allowBatchCancellation as allowBatchCancellation, 
		  		cc.tutorChatEnabled as tutorChatEnabled, 
		  		cc.approveEnrollmentsAutomatically as approveEnrollmentsAutomatically,
      			irp.name as institutionRegistrationPrefixName
			from Course c
				join CourseVersion cv on cv.course_uuid = c.uuid
				join CourseClass cc on cc.courseVersion_uuid = cv.uuid and cc.institution_uuid = ${institutionUUID}
			    left join InstitutionRegistrationPrefix irp on irp.uuid = cc.institutionRegistrationPrefixUUID
      	  	where cc.state <> ${CourseClassState.deleted.toString} and
      	  	    (cc.courseVersion_uuid = ${courseVersionUUID} or ${StringUtils.isNone(courseVersionUUID)}) and
      	  	    (cc.uuid = ${courseClassUUID} or ${StringUtils.isNone(courseClassUUID)}) and
		    	cc.institution_uuid = ${institutionUUID} and
	            (cv.name like ${filteredSearchTerm} or cc.name like ${filteredSearchTerm}) and 
	            (${StringUtils.isNone(adminUUID)} or
				(select count(*) from Role r where person_uuid = ${adminUUID} and (
					(r.role = ${RoleType.platformAdmin.toString} and r.institution_uuid = ${institutionUUID}) or 
					(r.role = ${RoleType.institutionAdmin.toString} and r.institution_uuid = ${institutionUUID}) or 
				( (r.role = ${RoleType.courseClassAdmin.toString} or r.role = ${RoleType.observer.toString} or r.role = ${RoleType.tutor.toString}) and r.course_class_uuid = cc.uuid)
			)) > 0)
      	  	order by cc.state, c.title, cv.versionCreatedAt desc, cc.name limit ${resultOffset}, ${pageSize};
		""".map[CourseClassTO](toCourseClassTO))
		courseClassesTO.setCount(
		    sql"""select count(cc.uuid) from CourseClass cc where cc.state <> ${CourseClassState.deleted.toString} and (${StringUtils.isSome(adminUUID)} and
					(select count(*) from Role r where person_uuid = ${adminUUID} and (
						(r.role = ${RoleType.platformAdmin.toString} and r.institution_uuid = ${institutionUUID}) or 
						(r.role = ${RoleType.institutionAdmin.toString} and r.institution_uuid = ${institutionUUID}) or 
						( (r.role = ${RoleType.courseClassAdmin.toString} or r.role = ${RoleType.observer.toString} or r.role = ${RoleType.tutor.toString}) and r.course_class_uuid = cc.uuid)
					)) > 0)
      	  	    and (cc.courseVersion_uuid = ${courseVersionUUID} or ${StringUtils.isNone(courseVersionUUID)})
      	  	    and (cc.uuid = ${courseClassUUID} or ${StringUtils.isNone(courseClassUUID)})
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
            	or cc.name like ${filteredSearchTerm}) and (${StringUtils.isSome(adminUUID)} and
				(select count(*) from Role r where person_uuid = ${adminUUID} and (
					(r.role = ${RoleType.platformAdmin.toString} and r.institution_uuid = ${institutionUUID}) or 
					(r.role = ${RoleType.institutionAdmin.toString} and r.institution_uuid = ${institutionUUID}) or 
					( (r.role = ${RoleType.courseClassAdmin.toString} or r.role = ${RoleType.observer.toString} or r.role = ${RoleType.tutor.toString}) and r.course_class_uuid = cc.uuid)
				)) > 0)
      	  	    and (cc.courseVersion_uuid = ${courseVersionUUID} or ${StringUtils.isNone(courseVersionUUID)})
      	  	    and (cc.uuid = ${courseClassUUID} or ${StringUtils.isNone(courseClassUUID)})
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

  private def isValidClass(cc: CourseClassTO): Boolean = {
    cc.getCourseClass().isPublicClass() || cc.getEnrollment() != null
  }

  private def isPlatformAdmin(institutionUUID: String, roles: List[Role]) = {
    var hasRole: Boolean = false
    roles.foreach(role => hasRole = hasRole
      || RoleCategory.isValidRole(role, RoleType.platformAdmin, institutionUUID, null))
    hasRole
  }

  private def isInstitutionAdmin(institutionUUID: String, roles: List[Role]) = {
    var hasRole: Boolean = isPlatformAdmin(institutionUUID, roles)
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

  private def isCourseClassObserver(courseClassUUID: String, institutionUUID: String, roles: List[Role]) = {
    var hasRole: Boolean = isInstitutionAdmin(institutionUUID, roles)
    roles.foreach(role => hasRole = hasRole
      || RoleCategory.isValidRole(role, RoleType.observer, null, courseClassUUID))
    hasRole
  }

  private def isCourseClassTutor(courseClassUUID: String, institutionUUID: String, roles: List[Role]) = {
    var hasRole: Boolean = isInstitutionAdmin(institutionUUID, roles)
    roles.foreach(role => hasRole = hasRole
      || RoleCategory.isValidRole(role, RoleType.tutor, null, courseClassUUID))
    hasRole
  }

  private def bindEnrollment(personUUID: String, courseClassTO: CourseClassTO) = {
    val enrollment = EnrollmentsRepo.byCourseClassAndPerson(courseClassTO.getCourseClass().getUUID(), personUUID)
    enrollment foreach courseClassTO.setEnrollment    
  }

  implicit def toString(rs: ResultSet): String = rs.getString(1)
}
