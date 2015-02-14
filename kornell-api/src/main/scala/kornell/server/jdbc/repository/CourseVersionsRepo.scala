package kornell.server.jdbc.repository

import java.sql.ResultSet
import scala.collection.JavaConverters._
import kornell.core.entity.Course
import kornell.core.entity.Course
import kornell.server.jdbc.SQL._
import kornell.server.repository.Entities._
import kornell.server.repository.TOs._
import kornell.core.entity.CourseVersion
import kornell.core.to.CourseVersionTO
import kornell.core.to.CourseVersionsTO
import kornell.core.util.UUID
import java.util.Date
import kornell.core.error.exception.EntityConflictException

object CourseVersionsRepo {
  
  def create(courseVersion: CourseVersion): CourseVersion = {  
    val courseVersionExists = sql"""
	    select count(*) from CourseVersion where course_uuid = ${courseVersion.getCourseUUID} and name = ${courseVersion.getName}
	    """.first[String].get
    if (courseVersionExists == "0") {  
	    if (courseVersion.getUUID == null){
	      courseVersion.setUUID(UUID.random)
	    }
	    if (courseVersion.getRepositoryUUID == null){
	      //pick default repository for the institution
	      courseVersion.setRepositoryUUID(sql"""
						| select distinct cr.uuid 
						| from S3ContentRepository cr
						| join Course c on c.institutionUUID = cr.institutionUUID
	    			| where c.uuid = ${courseVersion.getCourseUUID}
				    """.first[String].get)
	    }
			courseVersion.setVersionCreatedAt(new Date());
		
	    sql"""
	    | insert into CourseVersion (uuid,name,repository_uuid,course_uuid,versionCreatedAt,distributionPrefix,contentSpec,disabled) 
	    | values(
	    | ${courseVersion.getUUID},
	    | ${courseVersion.getName},
	    | ${courseVersion.getRepositoryUUID},
	    | ${courseVersion.getCourseUUID}, 
	    | ${courseVersion.getVersionCreatedAt},
	    | ${courseVersion.getDistributionPrefix},
	    | ${courseVersion.getContentSpec.toString},
	    | ${courseVersion.isDisabled})""".executeUpdate
	    courseVersion
    } else {
      throw new EntityConflictException("courseVersionAlreadyExists")
    }
  }  
  
  def byInstitution(institutionUUID: String) = newCourseVersionsTO(
    sql"""
	  	select cv.* from CourseVersion cv
		join Course c on cv.course_uuid = c.uuid
		where c.institutionUUID = $institutionUUID
		order by c.title, cv.versionCreatedAt desc
	  """.map[CourseVersion](toCourseVersion))
  
  def byCourse(courseUUID: String) = newCourseVersionsTO(
    sql"""
	  	select cv.* from CourseVersion cv
		join Course c on cv.course_uuid = c.uuid
		where cv.disabled = 0 and c.uuid = $courseUUID
		order by cv.versionCreatedAt desc
	  """.map[CourseVersion](toCourseVersion))
  
}