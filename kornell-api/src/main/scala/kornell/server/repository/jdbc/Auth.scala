package kornell.server.repository.jdbc

import kornell.server.repository.jdbc.SQLInterpolation._
import kornell.core.shared.data.Person
import java.sql.ResultSet
import kornell.server.repository.Beans
import javax.ws.rs.core.SecurityContext


object Auth extends Beans {
    //TODO: importing ScurityContext smells bad
	
  
	def withPerson[T](fun:Person => T)(implicit sc:SecurityContext):T = {
	    val username = sc.getUserPrincipal().getName()
	    implicit def toPerson(rs:ResultSet):Person = newPerson(rs.getString("uuid"),rs.getString("fullName"),rs.getString("lastPlaceVisited"));
	    
		val person:Option[Person] = sql"""
			select p.uuid, p.fullName, p.lastPlaceVisited 
			from Person p
			join Password pw on pw.person_uuid = p.uuid
			where pw.username = $username
		""".first[Person]
		
		if(person.isDefined)
		  fun(person.get)
		else throw new IllegalArgumentException(s"User [$username] not found.")
	}
	
	
	def setPassword(personUUID:String,username:String,password:String) = {
	  sql"""
	  	insert into Password (person_uuid,username,password)
	  	values ($personUUID,$username,$password)
	  	on duplicate key update
	  	username=$username,password=$password
	  """.executeUpdate
	}
}