package kornell.server.repository.jdbc
import kornell.server.repository.jdbc.SQLInterpolation._
import kornell.core.entity.Person
import java.sql.ResultSet
import kornell.server.repository.Entities
import kornell.server.repository.Entities._
import java.util.Date

class PersonRepository(val uuid:String) {
  
	def setPassword(username:String, password:String):PersonRepository = {
	  Auth.setPlainPassword(uuid, username, password)
	  this
	}
	
	def registerOn(institution_uuid:String):RegistrationRepository = {
	  RegistrationRepository(this, institution_uuid).register
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
	    rs.getString("confirmation"))
	    
	def get() = sql"""select * from Person where uuid=$uuid""".first[Person]

	def updatePerson(email: String, fullName:String, 
      company: String, title: String, sex: String, 
      birthDate: Date, confirmation: String) = {
	    sql"""
	    	update Person set fullName = $fullName,
	    	email = $email, company = $company, title = $title,
	    	sex = $sex, birthDate = $birthDate, confirmation = $confirmation
	    	where uuid = $uuid
	    """.executeUpdate
      this
    }
}

object PersonRepository{
  def apply(uuid:String) = new PersonRepository(uuid)
}