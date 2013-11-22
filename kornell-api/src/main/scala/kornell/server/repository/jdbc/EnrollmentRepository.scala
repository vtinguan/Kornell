package kornell.server.repository.jdbc

import kornell.core.entity.Enrollment
import java.sql.ResultSet
import kornell.server.repository.Entities
import kornell.server.repository.jdbc.SQLInterpolation._
import kornell.server.repository.Entities._
import kornell.core.entity.Enrollment
import kornell.core.entity.EnrollmentState


class EnrollmentRepository (person: PersonRepository, course_uuid: String) {

  def getPerson = person.get()

  def get: Enrollment = sql"""
	  select * from Enrollment 
	  were person_uuid=${person.uuid}
	   and course_uuid=${course_uuid}"""
    .first[Enrollment]
    .get

  def enroll() = {
    val uuid = randomUUID
    sql""" 
    	insert into Enrollment(uuid,course_uuid,person_uuid,enrolledOn)
    	values($randomUUID,$course_uuid,$person.uuid,now())
    """.executeUpdate
    None
  }

}

object EnrollmentRepository {
  def apply(person: PersonRepository, course_uuid: String) =
    new EnrollmentRepository(person, course_uuid)
}