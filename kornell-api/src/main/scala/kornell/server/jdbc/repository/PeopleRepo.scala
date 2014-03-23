package kornell.server.jdbc.repository

import kornell.core.entity.Person
import kornell.server.repository.Entities
import kornell.server.jdbc.SQL._
import kornell.server.repository.Entities._
import java.sql.ResultSet
import kornell.core.util.UUID
import kornell.server.repository.TOs

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
  
  def getByEmailOrCPF(username: String) = {
    sql"""
		select p.* from Person p
		where p.email = $username
		or p.cpf = $username
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

  def createPerson(email: String, fullName:String):Person = {
    create(
    		Entities.newPerson(null, fullName, null, email, null, null, null, null, null, null, null, null, null, null, null, null, null)
	  )
  }

  def createPersonCPF(cpf: String, fullName:String):Person = {
    create(
    		Entities.newPerson(null, fullName, null, null, null, null, null, null, null, null, null, null, null, null, null, null, cpf)
	  )
  }
  
  def create(person: Person):Person = {
    if(person.getUUID == null)
      person.setUUID(randUUID)
    sql""" 
    	insert into Person(uuid, fullName, email,
    		company, title, sex, birthDate, confirmation, cpf
    	) values (${person.getUUID},
             ${person.getFullName},
             ${person.getEmail},
             ${person.getCompany},
             ${person.getTitle},
             ${person.getSex},
             ${person.getBirthDate},
             ${person.getConfirmation},
             ${person.getCPF})
    """.executeUpdate
    person 
  }
}
