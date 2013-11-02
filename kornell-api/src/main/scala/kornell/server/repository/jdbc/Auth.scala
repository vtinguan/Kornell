package kornell.server.repository.jdbc

import kornell.server.repository.jdbc.SQLInterpolation._
import java.sql.ResultSet
import kornell.server.repository.Entities
import javax.ws.rs.core.SecurityContext
import org.apache.commons.codec.digest.DigestUtils
import kornell.server.repository.Entities._
import kornell.core.entity.Person

object Auth  { 
  //TODO: importing ScurityContext smells bad

  implicit def toPerson(rs: ResultSet): Person = newPerson(
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
      rs.getString("confirmation"))
      
  implicit def toString(rs: ResultSet): String = rs.getString("email")


  def withPerson[T](fun: Person => T)(implicit sc: SecurityContext): T = {
	val principal = if (sc != null) sc.getUserPrincipal else null
    val username =
      if (principal != null)
        sc.getUserPrincipal().getName()
      else "AUTH_SHOULD_HAVE_FAILED" //TODO
    
    val person: Option[Person] = getPerson(username)    
        
    if (person.isDefined)
      fun(person.get)
    else throw new IllegalArgumentException(s"User [$username] not found.")
  }
  
  //TODO: Cache
  def getPerson(username: String) = {
    sql"""
		select p.uuid, p.fullName, p.lastPlaceVisited,
		    p.email, p.firstName , p.lastName, p.company, 
		    p.title, p.sex, p.birthDate, p.confirmation
		from Person p
		join Password pw on pw.person_uuid = p.uuid
		where pw.username = $username
	""".first[Person]
  }
  
  def confirmAccount(personUUID: String) = {
    sql"""
		update Person set confirmation = ""
		where uuid = $personUUID
	""".executeUpdate
  }
  
  def getEmail(email: String) = {
    sql"""
    	select p.email from Person p
    	where p.email = $email
    """.first[String]
  }

  def setPlainPassword(personUUID: String, username: String, plainPassword: String) = {
    val digest = sha256(plainPassword)
    sql"""
	  	insert into Password (person_uuid,username,password)
	  	values ($personUUID,$username,$digest)
	  	on duplicate key update
	  	username=$username,password=$digest
	  """.executeUpdate
  }

  def sha256(plain: String): String = DigestUtils.sha256Hex(plain)

}
