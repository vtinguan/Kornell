package kornell.server.repository

import java.sql.Connection
import java.sql.ResultSet
import kornell.core.to.CourseTO
import kornell.core.entity.Enrollment
import kornell.server.repository.Entities._
import kornell.core.entity.EnrollmentState

package object jdbc {
  type UUID = String
  type ConnectionFactory = () => Connection
  def prop(name: String) = System.getProperty(name)

  val DEFAULT_URL = "jdbc:mysql:///ebdb"
  val DEFAULT_USERNAME= "kornell"
  val DEFAULT_PASSWORD= "42kornell73"


  implicit def toEnrollment(rs: ResultSet): Enrollment =
    newEnrollment(
      rs.getString("uuid"),
      rs.getDate("enrolledOn"),
      rs.getString("class_uuid"),
      rs.getString("person_uuid"),
      rs.getBigDecimal("progress"),
      rs.getString("notes"),
      EnrollmentState.valueOf(rs.getString("state")))
      
    

}