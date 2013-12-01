package kornell.server.repository.jdbc

import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.core.to.CourseTO
import kornell.server.repository.jdbc.SQLInterpolation._
import kornell.core.entity.CourseClass

object CourseClasses {
  def apply(uuid:String) = CourseClassRepository(uuid);

  def byInstitution(institutionUUID: String) =
    sql"""
    | select * from CourseClass where institution_uuid = $institutionUUID
    """.map[CourseClass](toCourseClass) 

}