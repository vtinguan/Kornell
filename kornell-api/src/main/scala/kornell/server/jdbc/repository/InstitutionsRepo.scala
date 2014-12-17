package kornell.server.jdbc.repository

import java.sql.ResultSet
import kornell.server.repository.Entities._
import kornell.server.repository.Entities
import kornell.core.entity.Person
import kornell.core.entity.Institution
import kornell.server.jdbc.SQL._
import kornell.server.repository.TOs
import javax.enterprise.context.ApplicationScoped
import java.lang.Boolean
import java.util.Date
import kornell.core.entity.BillingType

@ApplicationScoped
class InstitutionsRepo() {

  def create(
    uuid: String = randomUUID,
    name: String = null,
    fullName: String = null,
    terms: String = null,
    assetsURL: String = null,
    baseURL: String = null,
    demandsPersonContactDetails: Boolean = false,
    validatePersonContactDetails: Boolean = false,
    allowRegistration: Boolean = false,
    allowRegistrationByUsername: Boolean = false,
    activatedAt: Date = null,
    skin: String = null,
    billingType: BillingType): Institution = {
    create(Entities.newInstitution(uuid,
      name,
      fullName,
      terms,
      assetsURL,
      baseURL,
      demandsPersonContactDetails,
      validatePersonContactDetails,
      allowRegistration,
      allowRegistrationByUsername,
      activatedAt,
      skin,
      billingType))
  }

  def create(institution: Institution): Institution = {
    sql"""
    | insert into Institution (uuid,name,terms,assetsURL,baseURL,demandsPersonContactDetails,validatePersonContactDetails,fullName,allowRegistration,allowRegistrationByUsername,activatedAt,skin,billingType) 
    | values(
    | ${institution.getUUID},
    | ${institution.getName},
    | ${institution.getTerms},
    | ${institution.getAssetsURL},
    | ${institution.getBaseURL},
    | ${institution.isDemandsPersonContactDetails},
    | ${institution.isValidatePersonContactDetails},
    | ${institution.getFullName},
    | ${institution.isAllowRegistration},
    | ${institution.isAllowRegistrationByUsername},
    | ${institution.getActivatedAt},
    | ${institution.getSkin},
    | ${institution.getBillingType.toString})""".executeUpdate
    institution
  }

  def byUUID(UUID: String) =
    sql"select * from Institution where uuid = ${UUID}".first[Institution]

  def byName(institutionName: String) =
    sql"select * from Institution where name = ${institutionName}".first[Institution]

  def byHostName(hostName: String) =
    sql"""
      	| select i.* from Institution i 
      	| join InstitutionHostName ihn on i.uuid = ihn.institutionUUID
      	| where ihn.hostName = ${hostName}
	    """.first[Institution]

}