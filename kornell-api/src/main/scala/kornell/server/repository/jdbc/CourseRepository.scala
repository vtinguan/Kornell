package kornell.server.repository.jdbc

import java.sql.ResultSet
import kornell.core.entity.Course
import kornell.core.entity.Person
import kornell.server.repository.Entities.newCourse
import kornell.server.repository.jdbc.SQLInterpolation.SQLHelper

class CourseRepository(uuid: String) {

  def get() = sql"""select * from Course where uuid=$uuid""".first[Course]

  def withEnrollment(p: Person) = ???

}

object CourseRepository {
  def apply(uuid: String) = new CourseRepository(uuid)
}