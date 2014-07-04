package kornell.server.jdbc.repository

import java.sql.ResultSet
import org.apache.commons.codec.digest.DigestUtils
import javax.ws.rs.core.SecurityContext
import kornell.core.entity.Person
import kornell.core.entity.Role
import kornell.server.jdbc.SQL.SQLHelper
import kornell.server.repository.Entities.newPerson
import kornell.server.util.SHA256
import kornell.server.authentication.ThreadLocalAuthenticator
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.Response
import scala.collection.JavaConverters._

object AuthRepo {
  //TODO: importing SecurityContext smells bad

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
    rs.getString("postalCode"),
    rs.getString("cpf"))

  implicit def toString(rs: ResultSet): String = rs.getString(1)

  def getUserRoles = userRoles.asJava

  def userRoles = {
    val roles = ThreadLocalAuthenticator.getAuthenticatedPersonUUID
      .flatMap { usernameOf }
      .map { rolesOf }
      .getOrElse(Set.empty)
    roles
  }

  def withPerson[T](fun: Person => T): T = {
    val personUUID = ThreadLocalAuthenticator.getAuthenticatedPersonUUID
    personUUID match {
      case Some(personUUID) => {
        val person = PersonRepo(personUUID).first
        person match {
          case Some(one) => fun(one)
          case None => throw new IllegalArgumentException(s"Person [$personUUID] not found.")
        }
      }
      case None => throw new WebApplicationException(Response.Status.UNAUTHORIZED)
    }
  }

  def getPersonByPasswordChangeUUID(passwordChangeUUID: String) =
    sql"""
    	select p.* from Person p 
    	join Password pwd on pwd.person_uuid = p.uuid 
    	where pwd.requestPasswordChangeUUID = $passwordChangeUUID
    """.first[Person]

  def getUsernameByPersonUUID(personUUID: String) =
    sql"""
    	select pwd.username from Password pwd
    	where pwd.person_uuid = $personUUID
    """.first[String]

  def hasPassword(username: String) =
    sql"""
    	select pwd.username from Password pwd
    	where pwd.username = $username
    """.first[String].isDefined

  def setPlainPassword(personUUID: String, username: String, plainPassword: String) =
    sql"""
	  	insert into Password (person_uuid,username,password,requestPasswordChangeUUID)
	  	values ($personUUID,$username,${SHA256(plainPassword)}, null)
	  	on duplicate key update
	  	username=$username,password=${SHA256(plainPassword)},requestPasswordChangeUUID=null
	  """.executeUpdate

  def updateRequestPasswordChangeUUID(personUUID: String, requestPasswordChangeUUID: String) =
    sql"""
	  	update Password set requestPasswordChangeUUID = $requestPasswordChangeUUID
    	where person_uuid = $personUUID
	  """.executeUpdate

  //TODO: Change Roles table to reference Person UUID instead of username
  def rolesOf(username: String): Set[Role] = Set.empty ++ sql"""
  	select username,role,institution_uuid, course_class_uuid from Role where username = $username
  """.map[Role] { rs => toRole(rs) }

  def usernameOf(personUUID: String) = {
    val username = sql"""
  		select username from Password where person_uuid = $personUUID
  	""".first[String] { rs => rs.getString("username") }
    username
  }

  def authenticate(userkey: String, password: String) = 
    authByUsername(userkey, password)
    .orElse(authByCPF(userkey, password))
    .orElse(authByEmail(userkey, password))

  def authByEmail(email: String, password: String) = sql"""
   select person_uuid 
   from Password pwd
   join Person p on p.uuid = pwd.person_uuid
   where p.email=${email}
     and pwd.password=${SHA256(password)}
    """.first[String]

  def authByCPF(cpf: String, password: String) = sql"""
   select person_uuid 
   from Password pwd
   join Person p on p.uuid = pwd.person_uuid
   where p.cpf=${cpf}
     and pwd.password=${SHA256(password)}
    """.first[String]

    def authByUsername(username: String, password: String) = sql"""
    select person_uuid 
    from Password
    		where username=${username}
    		and password=${SHA256(password)}
    """.first[String]
}
