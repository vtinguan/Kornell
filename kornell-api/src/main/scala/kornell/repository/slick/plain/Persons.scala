package kornell.repository.slick.plain

import scala.slick.jdbc.GetResult
import scala.slick.jdbc.{StaticQuery => Q}
import scala.slick.jdbc.StaticQuery.interpolation
import scala.slick.session.Database
import scala.slick.session.Database.threadLocalSession
import scala.slick.session.Session
import kornell.repository.Beans
import kornell.core.shared.data.Person

object Persons extends Repository with Beans{
  
  implicit val getPerson = GetResult(r => newPerson(r.nextString,r.nextString))
  
  def create(fullname: String):Person = db.withSession {
    val p = newPerson(randUUID,fullname)    
    sqlu"insert into Person values (${p.getUUID} ,${p.getFullName})".execute    
    p
  }

  def foreach(f:Person => Unit) = db.withSession {
    sql"select uuid,fullName from Person".as[Person].foreach(f)
  }

  //TODO: Cache
  def byUsername(username:String) = db.withSession{
    sql"""
    select n.uuid,n.fullName
	from Principal p
	join Person n on p.person_uuid = n.uuid
	where p.username = ${username}
    """.as[Person].firstOption 
  }
}
