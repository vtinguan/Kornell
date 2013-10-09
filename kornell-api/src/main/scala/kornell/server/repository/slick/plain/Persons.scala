package kornell.server.repository.slick.plain

import scala.slick.jdbc.GetResult
import scala.slick.jdbc.{StaticQuery => Q}
import scala.slick.jdbc.StaticQuery.interpolation
import scala.slick.session.Database
import scala.slick.session.Database.threadLocalSession
import scala.slick.session.Session
import kornell.server.repository.Beans
import kornell.core.shared.data.Person
import javax.ws.rs.core.SecurityContext
import java.security.Principal
import kornell.server.repository.SlickRepository

import kornell.server.repository.Beans._


@Deprecated
object Persons extends SlickRepository {
  
  implicit val getPerson = GetResult(r => newPerson(r.nextString,r.nextString,r.nextString,
      r.nextString,r.nextString,r.nextString,
      r.nextString,r.nextString,r.nextString,
      r.nextDate,r.nextBoolean,r.nextBoolean,
      r.nextBoolean,r.nextBoolean,r.nextBoolean,
      r.nextBoolean,r.nextBoolean,r.nextBoolean))
  
  def create(fullname: String):Person = db.withSession {
    val person = newPerson(randUUID,fullname,null,
    		"","","","","","",null,true,true,true,true,true,true,true,true)    
    sqlu"insert into Person values (${person.getUUID} ,${person.getFullName},null)".execute    
    person
  }

  def foreach(f:Person => Unit) = db.withSession {
    sql"select uuid,fullName from Person".as[Person].foreach(f)
  }

  //TODO: Consider caching
  def byUsername(username:String) = db.withSession{
    sql"""
    select n.uuid,n.fullName,n.lastPlaceVisited
	from Password p
	join Person n on p.person_uuid = n.uuid
	where p.username = ${username}
    """.as[Person].firstOption 
  }
  
  
  def byUserPrincipal(implicit p:Principal) = byUsername(p.getName)
  
  
  
}
