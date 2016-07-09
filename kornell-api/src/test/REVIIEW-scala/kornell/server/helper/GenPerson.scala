package kornell.server.helper

import kornell.server.jdbc.repository.PeopleRepo
import kornell.server.repository.TOs
import kornell.server.api.UserResource
import kornell.core.entity.RegistrationType

trait GenPerson 
	extends GenInstitution
	with AuthSpec{

  
  def newPerson() = {
    val regreq = TOs.newRegistrationRequestTO(institutionUUID, randName, randEmail, randPassword,randCPF, registrationType = RegistrationType.email)
    UserResource().createUser(regreq).getPerson
  }
  
  val person = newPerson()
  
  val personUUID = person.getUUID
  
  def asPerson[T](fun : => T):T = asIdentity(personUUID)(fun)

}