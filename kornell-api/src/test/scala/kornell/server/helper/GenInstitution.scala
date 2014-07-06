package kornell.server.helper

import kornell.server.jdbc.repository.InstitutionsRepo
import kornell.server.repository.Entities

trait GenInstitution extends Generator {
  
  val institution = InstitutionsRepo.create(
      Entities.newInstitution(randUUID, randStr, randStr, randStr, randURL, randURL, false, null)
  )
  
  val institutionUUID = institution.getUUID()

}