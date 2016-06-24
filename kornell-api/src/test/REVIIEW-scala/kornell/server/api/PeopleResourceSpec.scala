package kornell.server.api

import org.junit.runner.RunWith
import kornell.server.test.UnitSpec
import kornell.server.helper.GenInstitution
import org.scalatest.junit.JUnitRunner
import kornell.server.helper.GenPerson

@RunWith(classOf[JUnitRunner])
class PeopleResourceSpec extends UnitSpec with GenPerson {
  
	"A person" should "be able to alter his own CPF" in asPerson {
		val self = UserResource().get(null, null, null).getUserInfoTO()
	  val ownCPF = self.getPerson.getCPF
	  assertResult(false){
	   new PersonResource(self.getPerson.getUUID).isRegistered(ownCPF,email = null)
	  }
	  	
	}

  
}