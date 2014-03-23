package kornell.server.api
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import kornell.core.to.RegistrationRequestTO
import kornell.server.repository.TOs
import scala.util.Random
import java.util.UUID
import javax.ws.rs.core.SecurityContext
import kornell.server.test.UnitSpec
import kornell.server.repository.Entities

@RunWith(classOf[JUnitRunner])
class RegistrationSpec extends UnitSpec {
  val userResource = new UserResource
  val institution = Entities.newInstitution(randUUID, randStr, randStr, randStr, randURL, randURL, false)
  val mockHttpServletResponse = new MockHttpServletResponse(0, "")

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