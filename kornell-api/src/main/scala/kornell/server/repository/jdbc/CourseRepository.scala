package kornell.server.repository.jdbc
import kornell.server.repository.TOs
import kornell.core.entity.Person
import kornell.server.repository.jdbc.SQLInterpolation.SQLHelper
import kornell.core.to.CourseTO
import java.sql.ResultSet
import kornell.server.repository.s3.S3

class CourseRepository(uuid: String) {
  implicit def newCourseTO(r: ResultSet): CourseTO = TOs.newCourseTO(
    r.getString("courseUUID"), r.getString("code"), r.getString("title"),
    r.getString("description"), r.getString("assetsURL"), r.getString("infoJson"),
    r.getString("enrollmentUUID"), r.getDate("enrolledOn"),
    r.getString("person_uuid"), r.getString("progress"), r.getString("repository_uuid"), r.getString("notes"))

  def withEnrollment(p: Person) = sql"""
		select c.uuid as courseUUID,c.code,c.title,c.description,c.assetsURL,c.infoJson,c.repository_uuid,
			   e.uuid as enrollmentUUID, e.enrolledOn,e.course_uuid,e.person_uuid,e.progress,e.notes
		from Course c
		left join Enrollment e on c.uuid = e.course_uuid
		where c.uuid = ${uuid} 
           and (e.person_uuid is null
		   or e.person_uuid = ${p.getUUID})
	""".first[CourseTO]
	

  def actomsVisitedBy(p: Person): List[String] = sql"""
  select actom_key from ActomEntered 
  where course_uuid = ${uuid}
  and person_uuid = ${p.getUUID}
  order by eventFiredAt
  """.map[String]({ rs => rs.getString("actom_key") })
}

object CourseRepository {
  def apply(uuid: String) = new CourseRepository(uuid)
}