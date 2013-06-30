package kornell.dev.data

import kornell.repository.slick.plain.Auth

class AuthData(p:PersonData) {  
  val principal = Auth.createUser(p.jack.getUUID, "jack", "black", List("user"))
}
