package kornell.server.repository.jdbc
import kornell.server.repository.jdbc.SQLInterpolation._
import kornell.core.shared.data.Person
import java.sql.ResultSet
import kornell.server.repository.Beans

class PersonRepository(val uuid:String) extends Beans {
  
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
	    rs.getString("firstName"),
	    rs.getString("lastName"),
	    rs.getString("company"),
	    rs.getString("title"),
	    rs.getString("sex"),
	    rs.getDate("birthDate"),
	    rs.getBoolean("usernamePrivate"),
	    rs.getBoolean("emailPrivate"),
	    rs.getBoolean("firstNamePrivate"),
	    rs.getBoolean("lastNamePrivate"),
	    rs.getBoolean("companyPrivate"),
	    rs.getBoolean("titlePrivate"),
	    rs.getBoolean("sexPrivate"),
	    rs.getBoolean("birthDatePrivate"))
	    
	def get() = sql"""select * from Person where uuid=$uuid""".first[Person]
}

object PersonRepository{
  def apply(uuid:String) = new PersonRepository(uuid)
}