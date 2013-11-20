package kornell.server.repository.jdbc

import kornell.core.entity.Registration
import java.sql.ResultSet
import kornell.server.repository.Entities
import kornell.server.repository.jdbc.SQLInterpolation._
import kornell.server.repository.Entities._
import kornell.core.entity.Enrollment
import kornell.core.entity.EnrollmentState

class RegistrationRepository(person: PersonRepository, institution_uuid: String) {

  def register = {
    Institutions.register(person.uuid, institution_uuid)
    this
  }

  def getPerson = person.get()

  implicit def toRegistration(rs: ResultSet): Registration =
    newRegistration(
      rs.getString("person_uuid"),
      rs.getString("institution_uuid"),
      rs.getDate("termsAcceptedOn"))

  def get: Registration = sql"""
	  select * from Registraton 
	  were person_uuid=${person.uuid}
	   and institution_uuid=${institution_uuid}"""
    .first[Registration]
    .get

  def requestEnrollment(course_uuid: String, person_uuid: String) = {
    val uuid = randomUUID
    sql""" 
    	insert into Enrollment(uuid,course_uuid,person_uuid,enrolledOn,state)
    	values($randomUUID,$course_uuid,$person_uuid,now(),${EnrollmentState.requested.toString()})
    """.executeUpdate
    None
  }

}

object RegistrationRepository {
  def apply(person: PersonRepository, institution_uuid: String) =
    new RegistrationRepository(person, institution_uuid)
}