package kornell.server.jdbc.repository

import java.sql.ResultSet
import java.util.concurrent.TimeUnit.MINUTES
import scala.collection.JavaConverters.setAsJavaSetConverter
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.Response
import kornell.core.entity.Person
import kornell.core.entity.Role
import kornell.server.authentication.ThreadLocalAuthenticator
import kornell.server.jdbc.SQL._
import kornell.server.repository.Entities.newPerson
import kornell.server.util.SHA256
import com.google.common.cache.LoadingCache
import scala.util.Try
import kornell.core.entity.RoleType
import kornell.core.error.exception.EntityNotFoundException
import kornell.core.error.exception.UnauthorizedAccessException
import org.mindrot.BCrypt

object AuthRepo {

  def apply() = new AuthRepo
  
  def lookup(institutionUUID: String, userkey: String) = 
   authByUsername(institutionUUID, userkey)
      .orElse(authByCPF(institutionUUID, userkey))
      .orElse(authByEmail(institutionUUID, userkey))
  
  implicit def toUsrValue(r: ResultSet): UsrValue = 
    (r.getString("password"), r.getString("person_uuid"), r.getBoolean("forcePasswordUpdate"))

  type UsrKey = (String, String)
  type UsrValue = (UsrPassword, PersonUUID, PasswordResetRequired) //1st String is bcrypt(sha256(password)), 2nd 
  type UsrPassword = String
  type PersonUUID = String
  type PasswordResetRequired = Boolean
  type PasswordCache = LoadingCache[UsrKey, Option[UsrValue]]

  def authByEmail(institutionUUID: String, email: String) = 
   sql"""
   select pwd.password as password, pwd.person_uuid, p.forcePasswordUpdate 
   from Password pwd
   join Person p on p.uuid = pwd.person_uuid
   where p.email=${email}
     and p.institutionUUID=${institutionUUID}
    """.first[UsrValue](toUsrValue)

  def authByCPF(institutionUUID: String, cpf: String) = 
   sql"""
   select pwd.password as password, pwd.person_uuid, p.forcePasswordUpdate 
   from Password pwd
   join Person p on p.uuid = pwd.person_uuid
   where p.cpf=${cpf}
     and p.institutionUUID=${institutionUUID}
    """.first[UsrValue](toUsrValue)

  def authByUsername(institutionUUID: String, username: String) = 
	sql"""
    select pwd.password, pwd.person_uuid, p.forcePasswordUpdate 
    from Password pwd
	join Person p on p.uuid = pwd.person_uuid
	where pwd.username=${username}
	and pwd.institutionUUID=${institutionUUID}
    """.first[UsrValue](toUsrValue)


  def usernameOf(personUUID: String) = {
    val username = sql"""
  		select username from Password where person_uuid = $personUUID
  	""".first[String] { rs => rs.getString("username") }
    username
  }

}

class AuthRepo() {

  type AuthValue = (String, Boolean)
  
  def authenticate(institutionUUID: String, userkey: String, password: String): Option[AuthValue] =  Try {
    val usrValue = AuthRepo.lookup(institutionUUID, userkey).get
    if (BCrypt.checkpw(SHA256(password), usrValue._1)) {
      Option((usrValue._2, usrValue._3))
    } else {
     None 
    }
  }.getOrElse(None)

  def withPerson[T](fun: Person => T): T = {
    val personUUID = ThreadLocalAuthenticator.getAuthenticatedPersonUUID
    personUUID match {
      case Some(personUUID) => {
        val person = PersonRepo(personUUID).first
        person match {
          case Some(one) => fun(one)
          case None => throw new EntityNotFoundException("personNotFound")
        }
      }
      case None => throw new UnauthorizedAccessException("authenticationFailed")
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
  
  def getPersonByUsernameAndPasswordUpdateFlag(username: String) = 
    sql"""
    	select p.* from Person p
    	join Password pwd on pwd.person_uuid = p.uuid
    	where pwd.username = ${username} and p.forcePasswordUpdate = true
  	""".first[Person]

  def hasPassword(institutionUUID: String, username: String) =
    sql"""
    	select pwd.username from Password pwd
    	where pwd.username = $username
    	and pwd.institutionUUID = $institutionUUID
    """.first[String].isDefined

  def updatePassword(personUUID: String, plainPassword: String, disableForceUpdatePassword: Boolean) = {
    sql"""
    	update Password set password=${BCrypt.hashpw(SHA256(plainPassword), BCrypt.gensalt())}, requestPasswordChangeUUID=null where person_uuid=${personUUID}
    """.executeUpdate
    if (disableForceUpdatePassword) {
      sql"""
    	   update Person set forcePasswordUpdate=false where uuid=${personUUID}
      """.executeUpdate
    }
  }
    
  def setPlainPassword(institutionUUID: String, personUUID: String, username: String, plainPassword: String) = {
    sql"""
	  	insert into Password (uuid,person_uuid,username,password,requestPasswordChangeUUID,institutionUUID)
	  	values (${randomUUID},$personUUID,$username,${BCrypt.hashpw(SHA256(plainPassword), BCrypt.gensalt())}, null, ${institutionUUID})
	  	on duplicate key update
	  	username=$username,password=${BCrypt.hashpw(SHA256(plainPassword), BCrypt.gensalt())},requestPasswordChangeUUID=null
	  """.executeUpdate
  }

  def updateRequestPasswordChangeUUID(personUUID: String, requestPasswordChangeUUID: String) =
    sql"""
	  	update Password set requestPasswordChangeUUID = $requestPasswordChangeUUID
    	where person_uuid = $personUUID
	  """.executeUpdate

  def grantPlatformAdmin(personUUID: String, institutionUUID: String) = {
    sql"""
	    	insert into Role (uuid, person_uuid, role, institution_uuid, course_class_uuid)
	    	values (${randomUUID}, ${personUUID}, 
	    	${RoleType.platformAdmin.toString}, 
	    	${institutionUUID}, 
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
