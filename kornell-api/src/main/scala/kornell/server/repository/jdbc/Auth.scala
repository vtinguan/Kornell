package kornell.server.repository.jdbc

import java.sql.ResultSet
import org.apache.commons.codec.digest.DigestUtils
import javax.ws.rs.core.SecurityContext
import kornell.core.entity.Person
import kornell.core.entity.Role
import kornell.core.entity.RoleType
import kornell.server.repository.Entities
import kornell.server.repository.Entities.newPerson
import kornell.server.repository.jdbc.SQLInterpolation.SQLHelper
import kornell.core.util.UUID

object Auth {
  //TODO: importing ScurityContext smells bad

  implicit def toPerson(rs: ResultSet): Person = newPerson(
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

  implicit def toString(rs: ResultSet): String = rs.getString(1) 
  
  def withPerson[T](fun: Person => T)(implicit sc: SecurityContext): T = {
    val principal = if (sc != null) sc.getUserPrincipal else null
    val username =
      if (principal != null)
        sc.getUserPrincipal().getName()
      else "AUTH_SHOULD_HAVE_FAILED" //TODO

    val person: Option[Person] = getPersonByEmail(username)

    if (person.isDefined)
      fun(person.get)
    else throw new IllegalArgumentException(s"User [$username] not found.")
  }

  //TODO: Cache
  def getPersonByEmail(email: String) = {
    //println(email)
    sql"""
		select * from Person p
		where p.email = $email
	""".first[Person]
  }

  //TODO: Cache
  def getPersonByPasswordChangeUUID(passwordChangeUUID: String) = 
    sql"""
		select p.*
		from Person p join Password pwd on pwd.person_uuid = p.uuid
		where pwd.requestPasswordChangeUUID = $passwordChangeUUID
	""".first[Person]

  def confirmAccount(personUUID: String) = 
    sql"""
		update Person set confirmation = ""
		where uuid = $personUUID
	""".executeUpdate
  

  def hasPassword(username: String) = 
    sql"""
    	select pwd.username from Password pwd
    	where pwd.username = $username
    """.first[String].isDefined
  

  def setPlainPassword(personUUID: String, username: String, plainPassword: String) = 
    sql"""
	  	insert into Password (person_uuid,username,password,requestPasswordChangeUUID)
	  	values ($personUUID,$username,${sha256(plainPassword)}, null)
	  	on duplicate key update
	  	username=$username,password=${sha256(plainPassword)},requestPasswordChangeUUID=null
	  """.executeUpdate
  

  def updateRequestPasswordChangeUUID(personUUID: String, requestPasswordChangeUUID: String) = 
    sql"""
	  	update Password set requestPasswordChangeUUID = $requestPasswordChangeUUID
    	where person_uuid = $personUUID
	  """.executeUpdate
  

  def sha256(plain: String): String = DigestUtils.sha256Hex(plain)

  def rolesOf(username: String) = sql"""
  	select username,role,institution_uuid from Role where username = $username
  """.map[Role] { rs =>
    val roleType = RoleType.valueOf(rs.getString("role"))
    val role = roleType match {
      case RoleType.user => Entities.newUserRole
      case RoleType.dean => Entities.newDeanRole(rs.getString("institution_uuid"))
    }
    role
  }

}
