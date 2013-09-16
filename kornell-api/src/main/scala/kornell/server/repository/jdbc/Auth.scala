package kornell.server.repository.jdbc

import kornell.server.repository.jdbc.SQLInterpolation._
import kornell.core.shared.data.Person
import java.sql.ResultSet
import kornell.server.repository.Beans
import javax.ws.rs.core.SecurityContext
import org.apache.commons.codec.digest.DigestUtils


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
	
	
	def setPlainPassword(personUUID:String, username:String, plainPassword:String) = {
	  val digest = sha256(plainPassword)
	  sql"""
	  	insert into Password (person_uuid,username,password)
	  	values ($personUUID,$username,$digest)
	  	on duplicate key update
	  	username=$username,password=$digest
	  """.executeUpdate
	}
	
	def sha256(plain:String):String = DigestUtils.sha256Hex(plain)
	
}