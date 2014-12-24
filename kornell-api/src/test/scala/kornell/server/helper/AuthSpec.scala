package kornell.server.helper

import org.scalatest.BeforeAndAfter
import kornell.server.auth.ThreadLocalAuthenticator

trait AuthSpec {
 
  def assumeIdentity(personUUID: String) =
    ThreadLocalAuthenticator.setAuthenticatedPersonUUID(personUUID)

  def yeldIdentity() =
    ThreadLocalAuthenticator.clearAuthenticatedPersonUUID

  def asIdentity[T](personUUID: String)(fun: => T):T =
    try {
      assumeIdentity(personUUID)
      fun
    } finally {
      yeldIdentity
    }
}