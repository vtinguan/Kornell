package kornell.server.test

import kornell.server.authentication.ThreadLocalAuthenticator
import kornell.core.entity.Person

trait PrivilegeEscalation {
  
  def runAs[T](p:Person)(f: => T):T = {
    assumeIdentity(p)
    val result = f
    assumeAnonymous
    result
  }
  
  def assumeIdentity(person:Person):Unit =
    assumeIdentity(person.getUUID)
  
  def assumeIdentity(personUUID: String):Unit =
    ThreadLocalAuthenticator.setAuthenticatedPersonUUID(personUUID)

  def assumeAnonymous() =
    ThreadLocalAuthenticator.clearAuthenticatedPersonUUID

  def asIdentity[T](personUUID: String)(fun: => T): T =
    try {
      assumeIdentity(personUUID)
      fun
    } finally {
      assumeAnonymous
    }
}