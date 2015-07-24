package kornell.server.jdbc.repository

import java.sql.ResultSet
import kornell.server.repository.Entities._
import kornell.server.repository.Entities
import kornell.core.entity.Person
import kornell.core.entity.Institution
import kornell.server.jdbc.SQL._
import kornell.server.repository.TOs
import kornell.core.entity.InstitutionType

object InstitutionsRepo {
  
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
  
  def byUUID(UUID:String) = 
	sql"select * from Institution where uuid = ${UUID}".first[Institution]
  
  def byName(institutionName:String) = 
	sql"select * from Institution where name = ${institutionName}".first[Institution]
  
  def byHostName(hostName:String) =
      sql"""
      	| select i.* from Institution i 
      	| join InstitutionHostName ihn on i.uuid = ihn.institutionUUID
      	| where ihn.hostName = ${hostName}
	    """.first[Institution]

  def byType(institutionType: InstitutionType) = 
    sql"""
        select * from Institution where institutionType = ${institutionType.toString}
    """.first[Institution]
}