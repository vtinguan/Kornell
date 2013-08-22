package kornell.server.repository.jdbc

import kornell.server.repository.jdbc.SQLInterpolation._ 
import kornell.core.shared.data.Person
import java.sql.ResultSet
import kornell.server.repository.Beans


object Auth extends Beans {
	def withPerson[T](fun:Person => T)(implicit username:String) = {
	    implicit def toPerson(rs:ResultSet):Person = newPerson(rs.getString("uuid"),rs.getString("fullName"));
	    
		val person:Option[Person] = sql"""
			select p.uuid, p.fullName 
			from Person p
			join Password pw on pw.person_uuid = p.uuid
			where pw.username = $username
		""".first[Person]
		
		if(person.isDefined)
		  fun(person.get)
		else throw new IllegalArgumentException(s"User [$username] not found.")
	}
}