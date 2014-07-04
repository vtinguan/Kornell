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
class RegistrationSpec extends UnitSpec with GenInstitution {

  "A new unconfirmed user" should "login sucessfully" in {
    val email = randEmail
    val password = randStr
    val regreq = TOs.newRegistrationRequestTO(institutionUUID, randName, email, password)
    val createdUUID = UserResource().createUser(regreq)
      .getPerson
      .getUUID
    val authUUID = AuthRepo.authenticate(email, password)
    authUUID should be (Some(createdUUID))
  }
  
  "A new user" should "login with either CPF, email or username" in {
  	val name = randName + " MultiLogin"
    val email = randEmail
    val username = randUsername
  	val passwd = randStr
  	val cpf = randCPF
  	val createdUUID = UserResource().createUser(institutionUUID,name,email,cpf,username,passwd)
  	val authUUIDCPF = AuthRepo.authenticate(cpf, passwd)
  	val authUUIDEmail = AuthRepo.authenticate(email, passwd)
  	val authUUIDUsername = AuthRepo.authenticate(username, passwd)
  	authUUIDCPF should be (Some(createdUUID))
  	authUUIDEmail should be (Some(createdUUID))
  	authUUIDUsername should be (Some(createdUUID))
  }
  
  //todo: CPF,email,username

}