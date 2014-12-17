package kornell.server.jdbc.repository

import scala.language.implicitConversions
import java.sql.ResultSet
import java.util.concurrent.TimeUnit.MINUTES
import scala.collection.JavaConverters.setAsJavaSetConverter
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.Response
import kornell.core.entity.Person
import kornell.core.entity.Role
import kornell.server.authentication.ThreadLocalAuthenticator
import kornell.server.jdbc.SQL._
import kornell.server.repository.Entities.newPerson
import kornell.server.util.SHA256
import scala.util.Try
import kornell.core.entity.RoleType

//TODO: URGENT: FIX CACHING
class AuthRepo(
   val peopleRepo:PeopleRepo) {

  def this() = this(null)
  
  val AUTH_CACHE_SIZE = 300;
 

 
  case class CredentialsNotFound() extends Exception

  def lookup(institutionUUID: String, userkey: String, password: String): Option[String] =
    authByUsername(institutionUUID, userkey, password)
      .orElse(authByCPF(institutionUUID, userkey, password))
      .orElse(authByEmail(institutionUUID, userkey, password))

  type UsrPwd = (String, String, String)
  type PersonUUID = String


  def authByEmail(institutionUUID: String, email: String, password: String) = sql"""
   select person_uuid 
   from Password pwd
   join Person p on p.uuid = pwd.person_uuid
   where p.email=${email}
     and pwd.password=${SHA256(password)}
     and p.institutionUUID=${institutionUUID}
    """.first[String]

  def authByCPF(institutionUUID: String, cpf: String, password: String) = sql"""
   select person_uuid 
   from Password pwd
   join Person p on p.uuid = pwd.person_uuid
   where p.cpf=${cpf}
     and pwd.password=${SHA256(password)}
     and p.institutionUUID=${institutionUUID}
    """.first[String]

  def authByUsername(institutionUUID: String, username: String, password: String) = sql"""
    select person_uuid 
    from Password
    		where username=${username}
    		and password=${SHA256(password)}
    		and institutionUUID=${institutionUUID}
    """.first[String]


  def lookupUserRoles(personUUID: Option[String]) = {
    val roles = personUUID
      .map { lookupRolesOf }
      .getOrElse(Set.empty)
    roles
  }

  def usernameOf(personUUID: String) = {
    val username = sql"""
  		select username from Password where person_uuid = $personUUID
  	""".first[String] { rs => rs.getString("username") }
    username
  }

  def lookupRolesOf(personUUID: String): Set[Role] = sql"""
  	select r.person_uuid, r.role, r.institution_uuid, r.course_class_uuid 
  	from Role r
  	where person_uuid = $personUUID
  """.map[Role] { rs => toRole(rs) }
  	 .toSet

  	 
  
  
  def authenticate(institutionUUID: String, userkey: String, password: String): Option[String] = ???

  def getUserRoles:java.util.Set[Role] = userRoles().asJava

  def userRoles(personUUID: Option[String]): Set[Role] = ???

  def userRoles(): Set[Role] = userRoles(ThreadLocalAuthenticator.getAuthenticatedPersonUUID)

  def withPerson[T](fun: Person => T): T = {
    val personUUID = ThreadLocalAuthenticator.getAuthenticatedPersonUUID
    personUUID match {
      case Some(personUUID) => {
        val person:Option[Person] = peopleRepo.byUUID(personUUID).first
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

  def hasPassword(institutionUUID: String, username: String) =
    sql"""
    	select pwd.username from Password pwd
    	where pwd.username = $username
    	and pwd.institutionUUID = $institutionUUID
    """.first[String].isDefined

  def setPlainPassword(institutionUUID: String, personUUID: String, username: String, plainPassword: String) = {
    sql"""
	  	insert into Password (uuid,person_uuid,username,password,requestPasswordChangeUUID,institutionUUID)
	  	values (${randomUUID},$personUUID,$username,${SHA256(plainPassword)}, null, ${institutionUUID})
	  	on duplicate key update
	  	username=$username,password=${SHA256(plainPassword)},requestPasswordChangeUUID=null
	  """.executeUpdate
    //    authCache.invalidate((username, plainPassword))
  }

  def updateRequestPasswordChangeUUID(personUUID: String, requestPasswordChangeUUID: String) =
    sql"""
	  	update Password set requestPasswordChangeUUID = $requestPasswordChangeUUID
    	where person_uuid = $personUUID
	  """.executeUpdate

  //TODO: Cache / remove external reference
  def rolesOf(personUUID: String) = lookupRolesOf(personUUID)

  def grantPlatformAdmin(personUUID: String) = {
    sql"""
	    	insert into Role (uuid, person_uuid, role, institution_uuid, course_class_uuid)
	    	values (${randomUUID}, ${personUUID}, 
	    	${RoleType.platformAdmin.toString}, 
	    	${null}, 
	    	${null})
		    """.executeUpdate
  }
  
  def grantInstitutionAdmin(personUUID:String,institutionUUID:String) = 
    sql"""
	    	insert into Role (uuid, person_uuid, role, institution_uuid, course_class_uuid)
	    	values (${randomUUID}, 
    		${personUUID}, 
	    	${RoleType.institutionAdmin.toString}, 
	    	${institutionUUID}, 
	    	${null} )
		    """.executeUpdate  
}
