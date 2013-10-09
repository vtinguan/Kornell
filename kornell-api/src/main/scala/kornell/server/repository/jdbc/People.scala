package kornell.server.repository.jdbc

import kornell.core.shared.data.Person
import kornell.server.repository.Beans
import kornell.server.repository.jdbc.SQLInterpolation._

import kornell.server.repository.Beans._
class People{
  def createPerson(fullName:String):PersonRepository = {
    val uuid = randomUUID
    sql"insert into Person(uuid, fullName) values ($uuid,$fullName)".executeUpdate 
    PersonRepository(uuid)
  }
}

object People {
  def apply() = new People()
}
