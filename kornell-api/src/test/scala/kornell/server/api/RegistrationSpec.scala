package kornell.server.api

import org.junit.runner.RunWith
import kornell.server.helper.MockSecurityContext
import kornell.server.helper.SimpleInstitution
import kornell.server.repository.TOs
import kornell.server.test.UnitSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class RegistrationSpec extends UnitSpec with SimpleInstitution{

  "A new user" should "login sucessfully (even if not confirmed yet)." in {
    val fullName = randName
    val email = randEmail
    val password = randStr
    val regreq = TOs.newRegistrationRequestTO(institution.getUUID, fullName, email, password)
    userResource.createUser(regreq)
    val userInfo = userResource.get(new MockSecurityContext(email), mockHttpServletResponse)
    userInfo should not be (None)
  }
  
  
  
}