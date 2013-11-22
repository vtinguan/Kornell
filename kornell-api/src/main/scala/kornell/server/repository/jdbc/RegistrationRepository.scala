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
    // don't register twice
    if(!get.isDefined)
    	Institutions.register(person.uuid, institution_uuid)
    this
  }

  def getPerson = person.get()

  implicit def toRegistration(rs: ResultSet): Registration =
    newRegistration(
      rs.getString("person_uuid"),
      rs.getString("institution_uuid"),
      rs.getDate("termsAcceptedOn"))

  def get: Option[Registration] = sql"""
	  select * from Registraton 
	  were person_uuid=${person.uuid}
	   and institution_uuid=${institution_uuid}"""
    .first[Registration]
}

object RegistrationRepository {
  def apply(person: PersonRepository, institution_uuid: String) =
    new RegistrationRepository(person, institution_uuid)
}