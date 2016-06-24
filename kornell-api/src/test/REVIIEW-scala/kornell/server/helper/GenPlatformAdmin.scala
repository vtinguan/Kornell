package kornell.server.helper

import kornell.server.jdbc.repository.PeopleRepo
import kornell.server.repository.Entities
import kornell.server.jdbc.repository.AuthRepo
import kornell.server.authentication.ThreadLocalAuthenticator

trait GenPlatformAdmin extends GenPerson with AuthSpec {
  val platformAdminUUID = {
    val personUUID = newPerson.getUUID
    AuthRepo().grantPlatformAdmin(personUUID, institution.getUUID())
    personUUID
  }

  def asPlatformAdmin[T](fun: => T):T = asIdentity(platformAdminUUID)(fun)

}