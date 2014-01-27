package kornell.server.jdbc.repository

import java.sql.ResultSet
import kornell.core.entity.Course
import kornell.core.entity.Person
import kornell.server.repository.Entities.newCourse
import kornell.server.jdbc.SQL._ 


class CourseRepo(uuid: String) {

  def get() = sql"""select * from Course where uuid=$uuid""".first[Course]

  def withEnrollment(p: Person) = ???

}

object CourseRepo {
  def apply(uuid: String) = new CourseRepo(uuid)
}