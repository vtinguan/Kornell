package kornell.server.repository.jdbc

import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.core.to.CourseTO
import kornell.server.repository.TOs.newCourseTO
import kornell.server.repository.jdbc.SQLInterpolation.SQLHelper

object Courses {
  def byUUID(uuid: String)(implicit @Context sc: SecurityContext): Option[CourseTO] = Auth.withPerson { p =>
    sql"""
		select c.uuid as courseUUID,c.code,c.title,c.description,c.assetsURL,c.infoJson,c.repository_uuid,
			   e.uuid as enrollmentUUID, e.enrolledOn,e.course_uuid,e.person_uuid,e.progress,e.notes
		from Course c
		left join Enrollment e on c.uuid = e.course_uuid
		where c.uuid = ${uuid} 
           and (e.person_uuid is null
		   or e.person_uuid = ${p.getUUID})
	"""
      .first[CourseTO]
  }
}