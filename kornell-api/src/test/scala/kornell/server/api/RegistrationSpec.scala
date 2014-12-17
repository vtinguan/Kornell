package kornell.server.api

import org.junit.runner.RunWith
import kornell.server.helper.MockSecurityContext
import kornell.server.helper.SimpleInstitution
import kornell.server.repository.TOs
import kornell.server.test.UnitSpec
import org.scalatest.junit.JUnitRunner
import kornell.server.jdbc.repository.AuthRepo
import kornell.server.helper.GenInstitution

@RunWith(classOf[JUnitRunner])
class RegistrationSpec {/*extends UnitSpec with GenInstitution {
  val ur:UserResource = ???
  val authRepo:AuthRepo = ???
  
  "A new unconfirmed user" should "login sucessfully" in {
    val email = randEmail
    val password = randStr
    val regreq = TOs.newRegistrationRequestTO(institutionUUID, randName, email, password)
    val createdUUID = ur.createUser(regreq)
      .getPerson
      .getUUID
    val authUUID = authRepo.authenticate(institutionUUID, email, password)
    authUUID should be (Some(createdUUID))
  }
  
  "A new user" should "login with either CPF, email or username" in {
  	val name = randName + " MultiLogin"
    val email = randEmail
    val username = randUsername
  	val passwd = randStr
  	val cpf = randCPF
  	val createdUUID = ur.createUser(institutionUUID,name,email,cpf,username,passwd)
  	val authUUIDUsername = authRepo.authenticate(institutionUUID, username, passwd)
  	val authUUIDCPF = authRepo.authenticate(institutionUUID, cpf, passwd)  	
  	val authUUIDEmail = authRepo.authenticate(institutionUUID, email, passwd)

  	authUUIDCPF should be (Some(createdUUID))
  	authUUIDEmail should be (Some(createdUUID))  	
  	authUUIDUsername should be (Some(createdUUID))
  }
  
  */
}

