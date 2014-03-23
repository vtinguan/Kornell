package kornell.server.jdbc.repository

import java.sql.ResultSet
import scala.collection.JavaConverters._
import kornell.core.entity.Course
import kornell.core.entity.Course
import kornell.server.jdbc.SQL._
import kornell.server.jdbc.SQL._
import kornell.server.repository.Entities._
import kornell.server.repository.TOs._
import kornell.core.entity.CourseVersion

object CourseVersionsRepo {

  def apply(uuid:String) = CourseVersionRepo(uuid)
  
  def create(courseVersion: CourseVersion): CourseVersion = {    
    sql"""
    | insert into CourseVersion (uuid,name,repository_uuid,course_uuid,versionCreatedAt,distributionPrefix) 
    | values(
    | ${courseVersion.getUUID},
    | ${courseVersion.getName},
    | ${courseVersion.getRepositoryUUID},
    | ${courseVersion.getCourseUUID}, 
    | ${courseVersion.getVersionCreatedAt},
    | ${courseVersion.getDistributionPrefix})""".executeUpdate
    courseVersion
  }  
  
  def byCourse(courseUUID: String) = newCourseVersionsTO(
    sql"""
	  	select cv.* from CourseVersion cv
		join Course c on cv.course_uuid = c.uuid
		where c.uuid = $courseUUID
	  """.map[CourseVersion](toCourseVersion))
  
}