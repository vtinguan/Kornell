package kornell.server.repository.jdbc

import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.core.to.CourseTO
import kornell.server.repository.jdbc.SQLInterpolation._
import kornell.core.entity.CourseClass
import kornell.core.entity.CourseClass
import java.sql.ResultSet
import kornell.server.repository.Entities

//TODO: Is it possible to move this import to package object?
import kornell.server.repository.jdbc.SQLInterpolation._

class CourseClasses {
}

object CourseClasses {
  
  def apply(uuid:String) = CourseClassRepository(uuid);

  def byInstitution(institutionUUID: String) =
    sql"""
    | select * from CourseClass where institution_uuid = $institutionUUID
    """.map[CourseClass](toCourseClass) 

}