package kornell.server.jdbc.repository

import kornell.core.entity.Person
import kornell.server.repository.Entities
import kornell.server.jdbc.SQL._
import kornell.server.repository.Entities._

object PeopleRepo {
  def createPerson(email: String, fullName:String, 
      company: String="", title: String="", sex: String="", 
      birthDate: String="1800-01-01", confirmation: String = "") = {
    
    val uuid = randUUID
    sql"""
    	insert into Person(uuid, fullName, email,
    		company, title, sex, birthDate, confirmation
    	) values ($uuid, $fullName, $email, 
    		$company, $title, $sex, $birthDate, $confirmation)
    """.executeUpdate
    PersonRepo(uuid)
  }

  def createPerson(email: String, fullName:String): PersonRepo = 
    createPerson(email, fullName, null, null, null, null, "")
}
