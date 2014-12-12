package kornell.server.helper

import kornell.server.jdbc.repository.InstitutionsRepo
import kornell.server.repository.Entities
import kornell.server.jdbc.repository.PeopleRepo
import kornell.server.jdbc.repository.AuthRepo
import kornell.core.entity.BillingType

trait GenInstitution extends Generator {
  
  val institution = InstitutionsRepo.create(
      Entities.newInstitution(randUUID, randStr, randStr, randStr, randURL, randURL, false, false, false, false, null, "", BillingType.enrollment)
  )
  
  val institutionUUID = institution.getUUID()
  

  
  
}