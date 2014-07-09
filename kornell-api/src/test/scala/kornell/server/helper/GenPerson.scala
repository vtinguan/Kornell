package kornell.server.helper

import kornell.server.jdbc.repository.PeopleRepo
import kornell.server.repository.TOs
import kornell.server.api.UserResource

trait GenPerson extends GenInstitution {
  def newPerson() = {
    val regreq = TOs.newRegistrationRequestTO(institutionUUID, randName, randEmail, randPassword)
    val createdUUID = UserResource().createUser(regreq)
      .getPerson
      .getUUID
    createdUUID
  }
}