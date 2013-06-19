package kornell.dev.data

import kornell.repository.slick.plain.Auth

trait AuthData extends BasicData {  
  val principal = Auth.createUser(fulano.getUUID, "fulano", "detal", List("user"))
}
