package kornell.server.helper

import javax.ws.rs.core.SecurityContext
import kornell.server.helper.MockPrincipal

class MockSecurityContext(val username: String) extends SecurityContext {
  override def getUserPrincipal(): MockPrincipal = new MockPrincipal(username)

  override def isUserInRole(role: String): Boolean = false

  override def isSecure: Boolean = false

  override def getAuthenticationScheme(): String = ???

}