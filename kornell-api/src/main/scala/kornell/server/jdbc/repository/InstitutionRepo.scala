package kornell.server.jdbc.repository

import kornell.core.entity.Institution
import kornell.server.jdbc.SQL._
import kornell.server.repository.TOs
import kornell.core.entity.InstitutionRegistrationPrefix
import kornell.core.entity.AuditedEntityType

class InstitutionRepo(uuid: String) {

  val finder = sql"select * from Institution where uuid=$uuid"

  def get = finder.get[Institution]
  def first = finder.first[Institution]
  
  
  def update(institution: Institution): Institution = { 
    //get previous version
    val oldInstitution = InstitutionRepo(institution.getUUID).first.get 
    
    sql"""
    | update Institution i
    | set i.name = ${institution.getName},
    | i.fullName = ${institution.getFullName},
    | i.terms = ${institution.getTerms},
    | i.assetsURL = ${institution.getAssetsURL},
    | i.baseURL = ${institution.getBaseURL},
    | i.demandsPersonContactDetails = ${institution.isDemandsPersonContactDetails},
    | i.validatePersonContactDetails = ${institution.isValidatePersonContactDetails},
    | i.allowRegistration = ${institution.isAllowRegistration},
    | i.allowRegistrationByUsername = ${institution.isAllowRegistrationByUsername},
    | i.activatedAt = ${institution.getActivatedAt},
    | i.skin = ${institution.getSkin},
    | i.billingType = ${institution.getBillingType.toString},
    | i.institutionType = ${institution.getInstitutionType.toString},
    | i.dashboardVersionUUID = ${institution.getDashboardVersionUUID},
    | i.internationalized = ${institution.isInternationalized},
    | i.useEmailWhitelist = ${institution.isUseEmailWhitelist},
    | i.assetsRepositoryUUID = ${institution.getAssetsRepositoryUUID},
    | i.timeZone = ${institution.getTimeZone()}
    | where i.uuid = ${institution.getUUID}""".executeUpdate
	    
    //log entity change
    EventsRepo.logEntityChange(institution.getUUID, AuditedEntityType.institution, institution.getUUID, oldInstitution, institution)
    
    institution
  }
  
  def getInstitutionRegistrationPrefixes = {
	  TOs.newInstitutionRegistrationPrefixesTO(sql"""
	    | select * from InstitutionRegistrationPrefix 
	    | where institutionUUID = ${uuid}
	        """.map[InstitutionRegistrationPrefix](toInstitutionRegistrationPrefix))
  }

}

object InstitutionRepo {
  def apply(uuid:String) = new InstitutionRepo(uuid)
}