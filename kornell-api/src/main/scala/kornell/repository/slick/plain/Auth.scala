package kornell.repository.slick.plain

import kornell.repository.Beans
import kornell.core.shared.data.Principal
import scala.slick.jdbc.StaticQuery.interpolation
import scala.slick.session.Database
import scala.slick.session.Session
import org.jboss.security.auth.spi.Util._
import scala.slick.session.Database.threadLocalSession

object Auth extends Repository with Beans {
  def hash(plain:String) = createPasswordHash("SHA-256", "BASE64", null, null, plain)
  
  //TODO: UPSERT
  def createUser(personUUID: String, username: String, plainPassword: String, roles: List[String]) =
    db.withTransaction { 
        val p = newPrincipal(randUUID, personUUID, username)
        sqlu"INSERT INTO Principal values (${p.getUUID}, ${p.getUsername}, ${p.getPersonUUID})".execute
        val passwordHash = hash(plainPassword)
        sqlu"INSERT INTO PasswordCredential values ($randUUID, $passwordHash,  ${p.getUUID})".execute
        roles foreach {role => sqlu"INSERT INTO Principal_Roles values (${p.getUUID}, $role)".execute }
        p       
    }
  
  
  
  
  

}