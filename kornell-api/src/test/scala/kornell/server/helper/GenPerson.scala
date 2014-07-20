package kornell.server.helper

import kornell.server.jdbc.repository.PeopleRepo
import kornell.server.repository.TOs
import kornell.server.api.UserResource

trait GenPerson 
	extends GenInstitution
	with AuthSpec{

  
  def newPerson() = {
    val regreq = TOs.newRegistrationRequestTO(institutionUUID, randName, randEmail, randPassword,randCPF)
    val createdUUID = UserResource().createUser(regreq)
      .getPerson
      .getUUID
    createdUUID
  }
  
  val personUUID = newPerson()
  
  def asPerson[T](fun : => T):T = asIdentity(personUUID)(fun)

}