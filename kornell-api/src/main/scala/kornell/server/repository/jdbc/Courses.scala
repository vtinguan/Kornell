package kornell.server.repository.jdbc

import java.math.BigDecimal
import java.sql.Timestamp
import java.util.Date
import scala.math.BigDecimal.javaBigDecimal2bigDecimal
import javax.ws.rs.core.SecurityContext
import kornell.core.shared.to.CourseTO
import kornell.core.shared.to.CoursesTO
import kornell.core.shared.data.Enrollment
import kornell.core.shared.data.Person
import kornell.core.shared.data.Course
import kornell.server.repository.SlickRepository
import kornell.server.repository.slick.plain.Persons
import kornell.server.repository.TOs
import kornell.server.repository.jdbc.SQLInterpolation._
import java.sql.ResultSet
import kornell.server.repository.s3.S3
import scala.collection.JavaConverters._
import javax.ws.rs.core._

object Courses extends TOs {
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
      .map { to =>        
        Option(to.getCourse.getRepositoryUUID).map { repository_uuid =>
          val s3 = S3(repository_uuid)
          val actoms = s3.actoms
          to.setBaseURL(s"http://${s3.bucket}.s3-sa-east-1.amazonaws.com/") //TODO: Resolve base url from region
          to.setActoms(actoms.asJava)
          to.getEnrollment().setLastActomVisited("");
        }
        to
      }
  }
}