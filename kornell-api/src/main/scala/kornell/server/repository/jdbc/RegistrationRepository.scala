package kornell.server.repository.jdbc

import kornell.core.shared.data.Registration
import java.sql.ResultSet
import kornell.server.repository.Beans
import kornell.server.repository.jdbc.SQLInterpolation._

class RegistrationRepository(person: PersonRepository, institution_uuid: String) extends Beans {

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

  def enrollOn(course_uuid: String) = {
    val uuid = randomUUID
    sql""" 
    	insert ignore into Enrollment(uuid,course_uuid,enrolledOn)
    	values($randomUUID,$course_uuid,now())
    """.executeUpdate
    None
  }

}

object RegistrationRepository {
  def apply(person: PersonRepository, institution_uuid: String) =
    new RegistrationRepository(person, institution_uuid)
}