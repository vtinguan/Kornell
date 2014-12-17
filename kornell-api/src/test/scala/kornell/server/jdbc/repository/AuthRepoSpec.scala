package kornell.server.jdbc.repository

import kornell.server.test.UnitSpec
import kornell.server.helper.GenInstitution
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import kornell.server.api.UserResource
import kornell.server.api.UserResource

@RunWith(classOf[JUnitRunner])
class AuthRepoSpec {/* extends UnitSpec with GenInstitution {
  var userResource:UserResource = ???
  var authRepo:AuthRepo = _
  var pwdCache:AuthRepo.PasswordCache = _
  var rolesCache:AuthRepo.RolesCache = _
  
  before {
    pwdCache = AuthRepo.newPasswordCache
	  rolesCache = AuthRepo.newRolesCache 
	  authRepo = AuthRepo(pwdCache,rolesCache)
  }
  
	"An AuthRepo" should "not authenticate random logins" in {
	  val auth = authRepo.authenticate(randUUID, randUsername, randPassword)
	  auth should be (None)
	} 
	
  it should "not cache auth failures" in {
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
	
	it should "retrieve granted privileges" in {
	  val username = randUsername
	  val password = randPassword
	  val personUUID = userResource.createUser(institutionUUID,randName,randEmail,randCPF,username,password)
	  AuthRepo().grantInstitutionAdmin(personUUID, institutionUUID)
	  val roles = AuthRepo().userRoles(Option(personUUID))
	}


}
*/}