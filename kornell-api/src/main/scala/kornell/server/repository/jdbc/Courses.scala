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

object Courses extends TOs {
  def byUUID(uuid: String): Option[CourseTO] =
    sql"""SELECT uuid,
    code,
    title,
    description,
    assetsURL,
    infoJson
  	FROM Course"""
    .first[CourseTO]
}