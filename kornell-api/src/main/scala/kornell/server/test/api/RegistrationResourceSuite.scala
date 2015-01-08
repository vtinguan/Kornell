package kornell.server.test.api

import org.junit.runner.RunWith
import org.jboss.arquillian.junit.Arquillian
import kornell.server.test.KornellSuite
import kornell.server.jdbc.repository.AuthRepo
import kornell.server.api.UserResource
import javax.inject.Inject
import kornell.server.test.Mocks
import org.junit.Test
import kornell.server.repository.TOs
import javax.annotation.PostConstruct

@RunWith(classOf[Arquillian])
class RegistrationResourceSuite extends KornellSuite {
  @Inject var ur:UserResource = _
  @Inject var authRepo:AuthRepo = _
  @Inject var mocks:Mocks = _
  
  @Test def unconfirmedUserShouldLoginSucessfully = {
    val email = randEmail
    val password = randStr
    val regreq = TOs.newRegistrationRequestTO(mocks.itt.getUUID, randName, email, password)
    val createdUUID = ur.createUser(regreq)
      .getPerson
      .getUUID
 
    val authUUID = authRepo.authenticate(mocks.itt.getUUID, email, password).get
    assert(authUUID == createdUUID)
  }
  
  @Test def newUserCanLoginWithEitherCPFEmailOrUsername =  {
  	val name = randName + " MultiLogin"
    val email = randEmail
    val username = randUsername
  	val passwd = randStr
  	val cpf = randCPF
  	val createdUUID = ur.createUser(mocks.itt.getUUID,name,email,cpf,username,passwd)
  	val authUUIDUsername = authRepo.authenticate(mocks.itt.getUUID, username, passwd).get
  	val authUUIDCPF = authRepo.authenticate(mocks.itt.getUUID, cpf, passwd).get
  	val authUUIDEmail = authRepo.authenticate(mocks.itt.getUUID, email, passwd).get

  	assert(authUUIDCPF == createdUUID)
  	assert(authUUIDEmail == createdUUID)
  	assert(authUUIDUsername == createdUUID)
  }
}