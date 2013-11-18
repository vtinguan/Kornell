package kornell.server.repository.jdbc

import kornell.server.repository.TOs
import kornell.core.to.RegistrationsTO
import kornell.server.repository.jdbc.SQLInterpolation._
import kornell.server.repository.Entities
import java.sql.ResultSet
import kornell.core.entity.Registration
import kornell.core.entity.Institution
import scala.collection.JavaConverters._
import kornell.core.entity.Person
import kornell.server.repository.Entities._
import kornell.server.repository.TOs._

class Registrations(personUUID: String, institutionUUID: String) {
  def acceptTerms() =
    sql"""update Registration
      	 set termsAcceptedOn=now()
      	 where person_uuid=$personUUID
      	   and institution_uuid=$institutionUUID
      	   """.executeUpdate
}

object Registrations {
  def toRegistration(rs: ResultSet) = newRegistration(
    rs.getString("person_uuid"),
    rs.getString("institution_uuid"),
    rs.getDate("termsAcceptedOn"))

  def toInstitution(rs: ResultSet) = newInstitution(
    rs.getString("institution_uuid"),
    rs.getString("name"),
    rs.getString("terms"),
    rs.getString("assetsURL"))

  //TODO: Use map instead of foreach
  def unsigned(implicit person: Person): RegistrationsTO = {
    val registrationsWithInstitutions = sql"""
	select r.person_uuid, r.institution_uuid, r.termsAcceptedOn, 
		i.name, i.terms, i.assetsURL
	from Registration r
	join Institution i  on r.institution_uuid = i.uuid
	where r.termsAcceptedOn is null
      and r.person_uuid=${person.getUUID}
	""".map { rs =>
      val r = toRegistration(rs)
      val i = toInstitution(rs)
      (r, i)
    }.toMap

    newRegistrationsTO(registrationsWithInstitutions)
  }

  def signingNeeded(implicit person: Person): Boolean =
    sql"""select count(*) 
	from Registration r
	join Institution i on r.institution_uuid=i.uuid
	where 
		i.terms is not null
	  and r.termsAcceptedOn is null
	  and r.person_uuid = ${person.getUUID}""".isPositive

  def apply(personUUID: String, uuid: String):Registrations =
    new Registrations(personUUID, uuid)
}

