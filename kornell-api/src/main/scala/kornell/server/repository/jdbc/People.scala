package kornell.server.repository.jdbc

import kornell.core.shared.data.Person
import kornell.server.repository.Beans
import kornell.server.repository.jdbc.SQLInterpolation._

import kornell.server.repository.Beans._
class People{
  def createTestPerson(fullName:String):PersonRepository = {
    val uuid = randomUUID
    sql"insert into Person(uuid, fullName) values ($uuid,$fullName)".executeUpdate 
    PersonRepository(uuid)
  }
  def createPerson(fullName:String):PersonRepository = ???
  
  def createPerson(email: String, firstName: String, lastName: String, company: String, title: String, sex: String, birthDate: String) = {
    val uuid = randomUUID
    val fullName = firstName + " " + lastName
    sql"""
    	insert into Person(uuid, fullName, email, firstName,
    		lastName, company, title, sex, birthDate
    	) values ($uuid, $fullName, $email, 
    		$firstName, $lastName, $company, $title, $sex, $birthDate)
    """.executeUpdate 
    PersonRepository(uuid)
  }
}

object People {
  def apply() = new People()
}
