package kornell.server.repository.jdbc

import java.sql.ResultSet
import kornell.core.entity.Course
import kornell.core.entity.Person
import kornell.server.repository.Entities.newCourse
import kornell.server.repository.jdbc.SQLInterpolation.SQLHelper

class CourseRepository(uuid: String) {

  def get() = sql"""select * from Course where uuid=$uuid""".first[Course]

  implicit def toCourse(rs: ResultSet): Course = newCourse(
    rs.getString("uuid"),
    rs.getString("code"),
    rs.getString("title"),
    rs.getString("description"),
    rs.getString("infoJson"),
    rs.getString("repository_uuid"))

  def withEnrollment(p: Person) = ???

  /* sql"""
		select c.uuid as courseUUID,c.code,c.title,c.description,c.infoJson,c.repository_uuid,
			   e.uuid as enrollmentUUID, e.enrolledOn,e.course_uuid,e.person_uuid,e.progress,e.notes
		from Course c
		left join Enrollment e on c.uuid = e.course_uuid
		where c.uuid = ${uuid}
           and (e.person_uuid is null
		   or e.person_uuid = ${p.getUUID})
	""".first[CourseTO]
  */

}

object CourseRepository {
  def apply(uuid: String) = new CourseRepository(uuid)
}