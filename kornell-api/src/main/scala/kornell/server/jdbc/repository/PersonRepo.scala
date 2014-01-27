package kornell.server.jdbc.repository
import kornell.server.jdbc.SQL._
import kornell.core.entity.Person
import java.sql.ResultSet
import kornell.server.repository.Entities
import kornell.server.repository.Entities._
import java.util.Date

class PersonRepo(val uuid:String) {
  
	def setPassword(username:String, password:String):PersonRepo = {
	  AuthRepo.setPlainPassword(uuid, username, password)
	  PersonRepo.this
	}
	
	def registerOn(institution_uuid:String):RegistrationRepo = {
	  (RegistrationRepo(PersonRepo.this, institution_uuid)).register
	}
	
	implicit def toPerson(rs:ResultSet):Person = newPerson(
	    rs.getString("uuid"),
	    rs.getString("fullName"), 
	    rs.getString("lastPlaceVisited"),
	    rs.getString("email"),
	    rs.getString("company"),
	    rs.getString("title"),
	    rs.getString("sex"),
	    rs.getDate("birthDate"),
	    rs.getString("confirmation"),
	    rs.getString("telephone"),
	    rs.getString("country"),
	    rs.getString("state"),
	    rs.getString("city"),
	    rs.getString("addressLine1"),
	    rs.getString("addressLine2"),
	    rs.getString("postalCode"))
	    
	def get = sql"""select * from Person where uuid=$uuid""".first[Person]

	def update(person: Person) = {
	    sql"""
	    	update Person set fullName = ${person.getFullName},
	    	email = ${person.getEmail}, company = ${person.getCompany}, title = ${person.getTitle},
	    	sex = ${person.getSex}, birthDate = ${person.getBirthDate}, confirmation = ${person.getConfirmation},
	    	telephone = ${person.getTelephone}, country = ${person.getCountry}, state = ${person.getState}, city = ${person.getCity}, 
	    	addressLine1 = ${person.getAddressLine1}, addressLine2 = ${person.getAddressLine2}, postalCode = ${person.getPostalCode}
	    	where uuid = $uuid
	    """.executeUpdate
      PersonRepo.this
    }
	
}

object PersonRepo{
  def apply(uuid:String) = new PersonRepo(uuid)
}