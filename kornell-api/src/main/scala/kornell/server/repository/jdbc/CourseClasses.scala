package kornell.server.repository.jdbc

import kornell.core.entity.CourseClass
import java.sql.ResultSet
import kornell.server.repository.Entities

//TODO: Is it possible to move this import to package object?
import kornell.server.repository.jdbc.SQLInterpolation._

class CourseClasses {
}

object CourseClasses {
  implicit def toCourseClass(rs:ResultSet):CourseClass = Entities.newCourseClass(
      rs.getString("uuid"),rs.getString("name"),
      rs.getString("courseVersion_uuid"),rs.getString("institution_uuid"))
    
  
  def apply(uuid:String) = CourseClassRepository(uuid);

  def byInstitution(institutionUUID: String) = sql"""
  select * from CourseClass where institution_uuid = $institutionUUID
  """.map[CourseClass]
}