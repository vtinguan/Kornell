package kornell.server.jdbc.repository

import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.core.to.CourseTO
import kornell.core.entity.CourseClass
import kornell.core.entity.CourseClass
import java.sql.ResultSet
import kornell.server.repository.Entities
import kornell.core.to.CourseClassTO
import kornell.server.repository.TOs
import kornell.server.jdbc.SQL._

class CourseClassesRepo {
}

object CourseClassesRepo {
  
  def apply(uuid:String) = CourseClassRepo(uuid);

  def byInstitution(institutionUUID: String) =
    sql"""
    | select * from CourseClass where institution_uuid = $institutionUUID
    """.map[CourseClass](toCourseClass) 
  
  def byPerson(personUUID: String) = {
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
			    cv.versionCreatedAt,
			    cc.uuid as courseClassUUID,
			    cc.name as courseClassName,
			    cc.institution_uuid as institutionUUID,
			    e.uuid as enrollmentUUID, 
			    e.enrolledOn, 
			    e.person_uuid as personUUID, 
			    e.progress,
			    e.notes,
			    e.state as enrollmentState
			from Course c
			left join CourseVersion cv on cv.course_uuid = c.uuid
			left join CourseClass cc on cc.courseVersion_uuid = cv.uuid
			left join Enrollment e on cc.uuid = e.class_uuid
			where e.person_uuid = ${personUUID};
		""".map[CourseClassTO](toCourseClassTO)
      )
  	}
  
  def byPersonAndInstitution(personUUID: String, institutionUUID: String) = {
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
			    cv.versionCreatedAt,
			    cc.uuid as courseClassUUID,
			    cc.name as courseClassName,
			    cc.institution_uuid as institutionUUID,
			    e.uuid as enrollmentUUID, 
			    e.enrolledOn, 
			    e.person_uuid as personUUID, 
			    e.progress,
			    e.notes,
			    e.state as enrollmentState
			from Course c
			left join CourseVersion cv on cv.course_uuid = c.uuid
			left join CourseClass cc on cc.courseVersion_uuid = cv.uuid
			left join Enrollment e on cc.uuid = e.class_uuid
			where e.person_uuid = ${personUUID}
		    and cc.institution_uuid = ${institutionUUID};
		""".map[CourseClassTO](toCourseClassTO)
      )
  	}

}