package kornell.server.jdbc.repository

import kornell.core.entity.Institution
import kornell.server.jdbc.SQL._
import kornell.server.repository.TOs

class InstitutionRepo(uuid: String) {
  
  def get = sql"""
          select * from Institution where uuid=$uuid
          """.get[Institution]
  
  def update(institution: Institution): Institution = {    
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
    | i.allowRegistration = ${institution.isAllowRegistrationByUsername},
    | i.activatedAt = ${institution.getActivatedAt},
    | i.skin = ${institution.getSkin}
    | where i.uuid = ${institution.getUUID}""".executeUpdate
    institution
  }
  
    def getRegistrationPrefixes = {
      TOs.newInstitutionRegistrationPrefixesTO(sql"""
        | select prefix from InstitutionRegistrationPrefix 
        | where institutionUUID = ${uuid}
            """.map[String])
  }

}

object InstitutionRepo {
  def apply(uuid:String) = new InstitutionRepo(uuid)
}