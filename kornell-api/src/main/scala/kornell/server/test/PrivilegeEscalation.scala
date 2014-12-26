package kornell.server.test

import kornell.server.auth.ThreadLocalAuthenticator
import kornell.core.entity.Person
import kornell.server.auth.ThreadLocalAuthenticator
import java.util.logging.Logger

trait PrivilegeEscalation {
  private final val logger = Logger.getLogger(classOf[PrivilegeEscalation].getName)

  def runAs[T](p: Person)(f: => T): Unit = try {
    assumeIdentity(p)
    f
  } finally assumeAnonymous

  def assumeIdentity(person: Person): Unit =
    assumeIdentity(person.getUUID)

  def assumeIdentity(personUUID: String): Unit =
    ThreadLocalAuthenticator.setAuthenticatedPersonUUID(personUUID)

  def assumeAnonymous() =
    ThreadLocalAuthenticator.clearAuthenticatedPersonUUID

}