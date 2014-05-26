package kornell.server.jdbc.repository

import kornell.core.entity.Person
import java.sql.ResultSet
import kornell.core.to.RegistrationsTO
import kornell.server.jdbc.SQL._
import kornell.server.repository.Entities._
import kornell.server.repository.TOs._
import kornell.core.entity.Registration

class RegistrationsRepo(personUUID: String, institutionUUID: String) {
  def acceptTerms() =
    sql"""update Registration
      	 set termsAcceptedOn=now()
      	 where person_uuid=$personUUID
      	   and institution_uuid=$institutionUUID
      	   """.executeUpdate
}

object RegistrationsRepo {
  def toRegistration(rs: ResultSet) = newRegistration(
    rs.getString("person_uuid"),
    rs.getString("institution_uuid"),
    rs.getDate("termsAcceptedOn"))

  def unsigned(implicit person: Person): RegistrationsTO = {
    val registrationList = sql"""
	select r.person_uuid, r.institution_uuid, r.termsAcceptedOn, 
		i.name, i.terms, i.assetsURL, i.baseURL
	from Registration r
	join Institution i  on r.institution_uuid = i.uuid
	where r.termsAcceptedOn is null
      and r.person_uuid=${person.getUUID}
	""".map[Registration](toRegistration)
    newRegistrationsTO(registrationList)
  }

  def getAll(implicit personUUID: String): RegistrationsTO = {
    val registrationList = sql"""
	select r.person_uuid, r.institution_uuid, r.termsAcceptedOn, 
		i.name, i.terms, i.assetsURL, i.baseURL
	from Registration r
	join Institution i  on r.institution_uuid = i.uuid
	where r.person_uuid=${personUUID}
	""".map[Registration](toRegistration)
    newRegistrationsTO(registrationList)
  }

  def signingNeeded(implicit person: Person): Boolean =
    sql"""select count(*) 
	from Registration r
	join Institution i on r.institution_uuid=i.uuid
	where 
		i.terms is not null
	  and r.termsAcceptedOn is null
	  and r.person_uuid = ${person.getUUID}""".isPositive

  def apply(personUUID: String, uuid: String):RegistrationsRepo =
    new RegistrationsRepo(personUUID, uuid)
}

