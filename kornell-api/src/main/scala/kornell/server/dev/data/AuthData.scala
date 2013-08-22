package kornell.server.dev.data

import kornell.server.repository.slick.plain.Auth

class AuthData(p:PersonData) {  
  val principal = Auth.createUser(p.jack.getUUID, "jack", "black", List("user"))
}
