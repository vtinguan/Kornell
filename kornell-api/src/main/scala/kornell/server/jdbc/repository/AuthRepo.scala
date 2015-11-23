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
  val AUTH_CACHE_SIZE = 300;

  val cacheBuilder = CacheBuilder
    .newBuilder()
    .expireAfterAccess(15, MINUTES)
    .maximumSize(AUTH_CACHE_SIZE)

  def apply(pwdCache: AuthRepo.PasswordCache, rolesCache: AuthRepo.RolesCache) =
    new AuthRepo(pwdCache, rolesCache)

  def apply() = new AuthRepo(newPasswordCache(), newRolesCache())

  val authLoader = new CacheLoader[UsrKey, Option[UsrValue]]() {
    override def load(auth: UsrKey): Option[UsrValue] =
      lookup(auth._1, auth._2) match {
        case s: Some[UsrValue] => s
        case None => throw new CredentialsNotFound
      }
  }

  case class CredentialsNotFound() extends Exception

  def lookup(institutionUUID: String, userkey: String) = 
   authByUsername(institutionUUID, userkey)
      .orElse(authByCPF(institutionUUID, userkey))
      .orElse(authByEmail(institutionUUID, userkey))
  
  
  implicit def toUsrValue(r: ResultSet): UsrValue = 
    (r.getString("password"), r.getString("person_uuid"), r.getBoolean("forcePasswordReset"))

  type UsrKey = (String, String)
  type UsrValue = (UsrPassword, PersonUUID, PasswordResetRequired) //1st String is bcrypt(sha256(password)), 2nd 
  type UsrPassword = String
  type PersonUUID = String
  type PasswordResetRequired = Boolean
  type PasswordCache = LoadingCache[UsrKey, Option[UsrValue]]
  type RolesCache = LoadingCache[Option[PersonUUID], Set[Role]]

  def newPasswordCache() = cacheBuilder.build(authLoader)
  def newRolesCache() = cacheBuilder.build(rolesLoader)

  def authByEmail(institutionUUID: String, email: String) = 
   sql"""
   select pwd.password as password, person_uuid, forcePasswordReset 
   from Password pwd
   join Person p on p.uuid = pwd.person_uuid
   where p.email=${email}
     and p.institutionUUID=${institutionUUID}
    """.first[UsrValue](toUsrValue)

  def authByCPF(institutionUUID: String, cpf: String) = 
   sql"""
   select pwd.password as password, person_uuid, forcePasswordReset 
   from Password pwd
   join Person p on p.uuid = pwd.person_uuid
   where p.cpf=${cpf}
     and p.institutionUUID=${institutionUUID}
    """.first[UsrValue](toUsrValue)

  def authByUsername(institutionUUID: String, username: String) = 
	sql"""
    select password, person_uuid, forcePasswordReset 
    from Password
	where username=${username}
	and institutionUUID=${institutionUUID}
    """.first[UsrValue](toUsrValue)
  

  val rolesLoader = new CacheLoader[Option[String], Set[Role]]() {
    override def load(personUUID: Option[String]): Set[Role] =
      lookupUserRoles(personUUID)
  }

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

}

class AuthRepo(pwdCache: AuthRepo.PasswordCache,
  rolesCache: AuthRepo.RolesCache) {

  type AuthValue = (String, Boolean)
  
  def authenticate(institutionUUID: String, userkey: String, password: String): Option[AuthValue] = Try {
    val usrValue = pwdCache.get((institutionUUID, userkey)).get
    if (BCrypt.checkpw(SHA256(password), usrValue._1)) {
      Option((usrValue._2, usrValue._3))
    } else {
     None 
    }
  }.getOrElse(None)

  def getUserRoles = userRoles().asJava

  def userRoles(personUUID: Option[String]) = rolesCache.get(personUUID)

  def userRoles(): Set[Role] = userRoles(ThreadLocalAuthenticator.getAuthenticatedPersonUUID)

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

  def hasPassword(institutionUUID: String, username: String) =
    sql"""
    	select pwd.username from Password pwd
    	where pwd.username = $username
    	and pwd.institutionUUID = $institutionUUID
    """.first[String].isDefined

  def updatePassword(personUUID: String, plainPassword: String) = {
    sql"""
    	update Password set password=${BCrypt.hashpw(SHA256(plainPassword), BCrypt.gensalt())}, requestPasswordChangeUUID=null where person_uuid=${personUUID}
    """.executeUpdate
  }
    
  def setPlainPassword(institutionUUID: String, personUUID: String, username: String, plainPassword: String) = {
    sql"""
	  	insert into Password (uuid,person_uuid,username,password,requestPasswordChangeUUID,institutionUUID)
	  	values (${randomUUID},$personUUID,$username,${BCrypt.hashpw(SHA256(plainPassword), BCrypt.gensalt())}, null, ${institutionUUID})
	  	on duplicate key update
	  	username=$username,password=${BCrypt.hashpw(SHA256(plainPassword), BCrypt.gensalt())},requestPasswordChangeUUID=null
	  """.executeUpdate
    //    authCache.invalidate((username, plainPassword))
  }

  def updateRequestPasswordChangeUUID(personUUID: String, requestPasswordChangeUUID: String) =
    sql"""
	  	update Password set requestPasswordChangeUUID = $requestPasswordChangeUUID
    	where person_uuid = $personUUID
	  """.executeUpdate

  //TODO: Cache / remove external reference
  def rolesOf(personUUID: String) = AuthRepo.lookupRolesOf(personUUID)

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
