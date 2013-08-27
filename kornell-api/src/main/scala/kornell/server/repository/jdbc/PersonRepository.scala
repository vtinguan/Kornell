package kornell.server.repository.jdbc
import kornell.server.repository.jdbc.SQLInterpolation._
import kornell.core.shared.data.Person
import java.sql.ResultSet
import kornell.server.repository.Beans

class PersonRepository(val uuid:String) extends Beans {
	def setPassword(username:String, password:String):PersonRepository = {
	  Auth.setPassword(uuid, username, password)
	  this
	}
	
	def registerOn(institution_uuid:String):RegistrationRepository = {
	  RegistrationRepository(this, institution_uuid).register
	}
	
	implicit def toPerson(rs:ResultSet):Person = newPerson(
	    rs.getString("uuid"),
	    rs.getString("fullName"), 
	    rs.getString("lastPlaceVisited"))
	    
	def get() = sql"""select * from Person where uuid=$uuid""".first[Person]
}

object PersonRepository{
  def apply(uuid:String) = new PersonRepository(uuid)
}