package kornell.server.jdbc.repository

import kornell.core.entity.Person
import kornell.server.repository.Entities
import kornell.server.jdbc.SQL._
import kornell.server.repository.Entities._
import java.sql.ResultSet

object PeopleRepo {
  
  implicit def toString(rs: ResultSet): String = rs.getString(1)
  
  //TODO Cache
  def getByUsername(username: String) = {
    sql"""
		select p.* from Person p
		join Password pwd
		on p.uuid = pwd.person_uuid
		where pwd.username = $username
	""".first[Person]
  }
  
  def findBySearchTerm(search: String) ={ 
    println("dfsaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa " + search )
    newPeople(
    sql"""
      	| select p.* from Person p 
      	| where p.email like ${search + "%"}
      	| or p.cpf like ${search + "%"}
      	| order by p.email, p.cpf
      	| limit 8
	    """.map[Person](toPerson))
  }
	    
  def createPerson(email: String, fullName:String, 
      company: String="", title: String="", sex: String="", 
      birthDate: String="1800-01-01", confirmation: String = "", cpf: String) = {
    
    val uuid = randUUID
    sql"""
    	insert into Person(uuid, fullName, email,
    		company, title, sex, birthDate, confirmation, cpf
    	) values ($uuid, $fullName, $email, 
    		$company, $title, $sex, $birthDate, $confirmation, $cpf)
    """.executeUpdate
    PersonRepo(uuid)
  }

  def createPerson(email: String, fullName:String): PersonRepo = 
    createPerson(email, fullName, null, null, null, null, "", null)
    
  def createPersonCPF(cpf: String, fullName:String): PersonRepo = 
    createPerson(null, fullName, null, null, null, null, "", cpf)
}
