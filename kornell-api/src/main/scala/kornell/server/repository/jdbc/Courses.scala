package kornell.server.repository.jdbc

import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.core.to.CourseTO
import kornell.server.repository.jdbc.SQLInterpolation._
import kornell.core.entity.Course

object Courses {
  @Deprecated //use withEnrollment
  def byUUID(uuid: String)(implicit @Context sc: SecurityContext): Option[CourseTO] =
    Auth.withPerson { p => apply(uuid).withEnrollment(p) }

  def apply(uuid:String) = CourseRepository(uuid)
  
  def byCourseClassUUID(courseClassUUID: String) = sql"""
	  select * from Course c join
	  CourseVersion cv on cv.course_uuid = c.uuid join
	  CourseClass cc on cc.courseVersion_uuid = cv.uuid where cc.uuid = $courseClassUUID
  """.first[Course]
}