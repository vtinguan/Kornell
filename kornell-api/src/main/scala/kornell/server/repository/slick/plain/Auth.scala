package kornell.server.repository.slick.plain

import scala.slick.jdbc.StaticQuery.interpolation
import scala.slick.session.Database
import scala.slick.session.Database.threadLocalSession
import scala.slick.session.Session
import kornell.core.shared.data.Principal
import kornell.server.repository.Beans
import kornell.server.repository.SlickRepository

object Auth extends SlickRepository with Beans {
  def hash(plain:String) = plain
  
  //TODO: UPSERT
  def createUser(personUUID: String, username: String, plainPassword: String, roles: List[String]) =
    db.withTransaction { 
        val p = newPrincipal(randUUID, personUUID, username)

        sqlu"""insert into Password(username,password,person_uuid) 
        	   values ($username, $plainPassword,  $personUUID)""".execute
        	   
        roles foreach {role => sqlu"INSERT INTO Role values ($username, $role)".execute }
        p       
    }
  
  
  
  
  

}