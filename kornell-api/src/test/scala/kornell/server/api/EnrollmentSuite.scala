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

@RunWith(classOf[JUnitRunner])
class EnrollmentSuite extends FunSuite  {
  val userResource = new UserResource 
  
  
  test("Not pre-enrolled user registration") {
    val name = UUID.randomUUID.toString
    val password = chars.take(12).toString
    val email = chars.take(5) + "@" + chars.take(5) + ".com"
    val regreq = TOs.newRegistrationRequestTO
    regreq.setEmail(email)
    regreq.setFullName(name)
    val institutionUUID = "00a4966d-5442-4a44-9490-ef36f133a259" //TODO register @before
    regreq.setInstitutionUUID(institutionUUID)
    regreq.setPassword(password)
    userResource.createUser(regreq)
    
    //TODO: Can he log in?
    assert(false == false) 
  }
  
  lazy val chars = Stream.continually(Random.nextPrintableChar)
  
  

}