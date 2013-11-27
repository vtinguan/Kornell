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
class RegistrationSpec extends UnitSpec  {
  val userResource = new UserResource 
  val institution = Entities.newInstitution(randUUID, randStr, randStr, randURL, randURL)
  
  "A new user" should "login sucessfully (even if not confirmed yet)." in {      
      val fullName = randName
      val email = randEmail
      val password = randStr
	  val regreq = TOs.newRegistrationRequestTO(institution.getUUID, fullName, email, password)
	  userResource.createUser(regreq)
	  val userInfo = userResource.get(new MockSecurityContext(email))
	  userInfo should not be (None)
  }
  
  
  /*
  test("Not pre-enrolled user registration") {
    val name = UUID.randomUUID.toString
    val password = chars.take(12).toString
    val email = name + "@" + chars.take(5) + ".com"
    val regreq = TOs.newRegistrationRequestTO
    regreq.setEmail(email)
    regreq.setFullName(name)
    val institutionUUID = "00a4966d-5442-4a44-9490-ef36f133a259" //TODO register @before
    regreq.setInstitutionUUID(institutionUUID)
    regreq.setPassword(password)
    
    
    //TODO: Can he log in?
    assert(false,"???") 
  }
  
  */ 
  

}