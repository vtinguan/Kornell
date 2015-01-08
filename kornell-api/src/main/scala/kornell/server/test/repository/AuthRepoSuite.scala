package kornell.server.test.repository

import org.junit.runner.RunWith
import kornell.server.test.KornellSuite
import org.jboss.arquillian.junit.Arquillian
import kornell.server.jdbc.repository.AuthRepo
import kornell.server.api.UserResource
import javax.inject.Inject
import kornell.server.jdbc.repository.AuthRepo
import org.junit.Test
import kornell.server.test.Mocks
@RunWith(classOf[Arquillian])
class AuthRepoSuite extends KornellSuite {
  @Inject var userResource: UserResource = _
  @Inject var authRepo: AuthRepo = _
  @Inject var mocks:Mocks = _

  @Test def canNotAuthenticateRandomLogins = {
    val auth = authRepo.authenticate(randUUID, randUsername, randPassword)
    assert(auth.isEmpty)
  }
  /*
  @Test def doNotCcache auth failures" in {
	  val username = randUsername
	  val password = randPassword
	  pwdCache.size should be (0)
	  val miss = authRepo.authenticate(randUUID, username, password)
	  miss should be (None)
	  pwdCache.size should be (0)	  
	}
	
	it should "cache on auth success" in {
	  val username = randUsername
	  val password = randPassword
	  val personUUID = userResource.createUser(institutionUUID,randName,randEmail,randCPF,username,password)		
	  pwdCache.size should be (0)	  
	  val hit = authRepo.authenticate(institutionUUID,username, password)
		hit should be (Some(personUUID))
		pwdCache.size() should be (1)
	}
	
	*/
	@Test def mustRetrieveGrantedPrivileges = {
	  val username = randUsername
	  val password = randPassword
	  val personUUID = userResource.createUser(mocks.itt.getUUID,randName,randEmail,randCPF,username,password)
	  authRepo.grantInstitutionAdmin(personUUID, mocks.itt.getUUID)
	  val roles = authRepo.userRoles(Option(personUUID))
	}


}