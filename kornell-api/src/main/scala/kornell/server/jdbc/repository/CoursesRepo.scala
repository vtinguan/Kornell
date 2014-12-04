package kornell.server.jdbc.repository

import java.sql.ResultSet
import scala.collection.JavaConverters._
import kornell.core.entity.Course
import kornell.core.entity.Course
import kornell.server.jdbc.SQL._
import kornell.server.jdbc.SQL._
import kornell.server.repository.Entities._
import kornell.server.repository.TOs._
import kornell.server.repository.Entities

object CoursesRepo {

  def create(course: Course): Course = {    
    sql"""
    | insert into Course (uuid,code,title,description,infoJson) 
    | values(
    | ${course.getUUID},
    | ${course.getCode},
    | ${course.getTitle}, 
    | ${course.getDescription},
    | ${course.getInfoJson})""".executeUpdate
    course
  }  
  
  def byCourseClassUUID(courseClassUUID: String) = sql"""
	  select * from Course c join
	  CourseVersion cv on cv.course_uuid = c.uuid join
	  CourseClass cc on cc.courseVersion_uuid = cv.uuid where cc.uuid = $courseClassUUID
  """.first[Course]
  
  def byInstitution(institutionUUID: String) = newCoursesTO(
    sql"""
	  	select c.* from Course c
		join Institution i on c.institutionUUID = i.uuid
		where c.institutionUUID = $institutionUUID
	  """.map[Course](toCourse))
  
}