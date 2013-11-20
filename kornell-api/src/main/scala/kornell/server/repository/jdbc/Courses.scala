package kornell.server.repository.jdbc

import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.core.to.CourseTO
import kornell.server.repository.jdbc.SQLInterpolation._

object Courses {
  @Deprecated //use withEnrollment
  def byUUID(uuid: String)(implicit @Context sc: SecurityContext): Option[CourseTO] =
    Auth.withPerson { p => apply(uuid).withEnrollment(p) }

  def apply(uuid:String) = CourseRepository(uuid)

  def byInstitution(institutionUUID: String) = {
    sql"""
    | select * from Course where institution_uuid = $institutionUUID
    """.map[CourseTO](newCourseTO)   
  }
  
}