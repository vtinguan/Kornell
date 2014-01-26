package kornell.server.jdbc.repository

import kornell.core.entity.Registration
import java.sql.ResultSet
import kornell.server.repository.Entities
import kornell.server.jdbc.SQL._
import kornell.server.repository.Entities._
import kornell.core.entity.Enrollment
import kornell.core.entity.EnrollmentState

class RegistrationRepo(person: PersonRepo, institution_uuid: String) {

  def register = {
    // don't register twice
    if(!get.isDefined)
    	InstitutionsRepo.register(person.uuid, institution_uuid)
    RegistrationRepo.this
  }

  def getPerson = person.get

  implicit def toRegistration(rs: ResultSet): Registration =
    newRegistration(
      rs.getString("person_uuid"),
      rs.getString("institution_uuid"),
      rs.getDate("termsAcceptedOn"))

  def get: Option[Registration] = sql"""
	  select * from Registration 
	  where person_uuid=${person.uuid}
	   and institution_uuid=${institution_uuid}"""
    .first[Registration]
}

object RegistrationRepo {
  def apply(person: PersonRepo, institution_uuid: String) =
    new RegistrationRepo(person, institution_uuid)
}