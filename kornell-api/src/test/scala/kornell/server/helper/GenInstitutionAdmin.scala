package kornell.server.helper

import kornell.server.jdbc.repository.AuthRepo

trait GenInstitutionAdmin 
extends  GenPerson
with AuthSpec {
  /*
  val authRepo:AuthRepo = ???
  
  val institutionAdminUUID = {
    val person = newPerson().getUUID
    authRepo.grantInstitutionAdmin(person,institution.getUUID())
    person
  }
  
  def asInstitutionAdmin[T](fun : => T):T = asIdentity(institutionAdminUUID)(fun)
 */
}