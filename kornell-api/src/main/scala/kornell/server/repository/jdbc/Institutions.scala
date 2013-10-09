package kornell.server.repository.jdbc

import java.sql.ResultSet
import kornell.server.repository.Beans._

import kornell.core.shared.data.Institution
import kornell.core.shared.data.Person
import kornell.core.shared.data.Registration
import kornell.server.repository.Beans
import kornell.server.repository.jdbc.SQLInterpolation.SQLHelper

object Institutions {

  def create(name: String, terms: String): Institution = {
    val i = newInstitution(randomUUID, name, terms, "")
    sql"""
    | insert into Institution(uuid,name,terms) 
    | values ($i.getUUID,$i.getName,$i.terms)""".executeUpdate
    i
  }

  def register(p: Person, i: Institution):Registration = {
    val r = newRegistration(p, i)
    register(p.getUUID,i.getUUID)
    r
  }
  
  def register(p: String, i:String):Unit = {    
    sql"""
    | insert into Registration(person_uuid,institution_uuid)
    | values ($p, $i)
    """.executeUpdate    
  }
  
  implicit def toInstitution(rs:ResultSet) = 
    newInstitution(rs.getString("uuid"), 
        rs.getString("name"), 
        rs.getString("terms"),
        rs.getString("assetsURL")) 
  
  def byUUID(UUID:String) = 
	sql"select * from Institution".first[Institution]

  
  def usersInstitution(implicit person:Person) = 
	sql"""select * 
	from Registration r
	join Institution i on r.institution_uuid=i.uuid
	where r.person_uuid = ${person.getUUID}""".first[Institution]

}