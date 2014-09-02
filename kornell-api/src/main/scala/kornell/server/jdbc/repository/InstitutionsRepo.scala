package kornell.server.jdbc.repository

import java.sql.ResultSet
import kornell.server.repository.Entities._
import kornell.server.repository.Entities
import kornell.core.entity.Person
import kornell.core.entity.Institution
import kornell.core.entity.Registration
import kornell.server.jdbc.SQL._

object InstitutionsRepo {
  
  def create(institution: Institution): Institution = {    
    sql"""
    | insert into Institution (uuid,name,terms,assetsURL,baseURL,demandsPersonContactDetails,fullName,activatedAt,skin) 
    | values(
    | ${institution.getUUID},
    | ${institution.getName},
    | ${institution.getTerms},
    | ${institution.getAssetsURL},
    | ${institution.getBaseURL},
    | ${institution.isDemandsPersonContactDetails},
    | ${institution.getFullName},
    | ${institution.getActivatedAt},
    | ${institution.getSkin})""".executeUpdate
    institution 
  }  
  
  def update(institution: Institution): Institution = {    
    sql"""
    | update Institution i
    | set i.name = ${institution.getName},
    | i.fullName = ${institution.getFullName},
    | i.terms = ${institution.getTerms},
    | i.assetsURL = ${institution.getAssetsURL},
    | i.baseURL = ${institution.getBaseURL},
    | i.demandsPersonContactDetails = ${institution.isDemandsPersonContactDetails},
    | i.activatedAt = ${institution.getActivatedAt},
    | i.skin = ${institution.getSkin}
    | where i.uuid = ${institution.getUUID}""".executeUpdate
    institution
  }
  
  def register(p: Person, i: Institution):Registration = {
    val r = newRegistration(p, i)
    register(p.getUUID,i.getUUID)
    r
  }
  
  def register(p: String, i:String):Unit = {    
    sql"""
    | insert into Registration(person_uuid,institution_uuid)
    | values ($p, $i)
    """.executeUpdate    
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

}