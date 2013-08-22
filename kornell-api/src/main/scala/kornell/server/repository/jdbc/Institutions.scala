package kornell.server.repository.jdbc

import kornell.core.shared.data.Institution
import kornell.server.repository.Beans
import kornell.server.repository.SlickRepository
import kornell.core.shared.data.Institution
import kornell.core.shared.data.Person
import kornell.core.shared.data.Registration
import kornell.server.repository.jdbc.SQLInterpolation._ 
import java.sql.ResultSet

object Institutions extends  Beans {

  def create(name: String, terms: String): Institution = {
    val i = newInstitution(randomUUID, name, terms)
    sql"""
    | insert into Institution(uuid,name,terms) 
    | values ($i.getUUID,$i.getName,$i.terms)""".executeUpdate
    i
  }

  def register(p: Person, i: Institution): Registration = {
    val r = Registration(p, i)
    sql"""
    | insert into Registration(person_uuid,institution_uuid)
    | values ($p.getUUID, $i.getUUID)
    """.executeUpdate
    r
  }
  
  implicit def toInstitution(rs:ResultSet) = 
    newInstitution(rs.getString("uuid"), 
        rs.getString("name"), 
        rs.getString("terms")) 
  
  def byUUID(UUID:String) = 
	sql"select * from Institution".first[Institution]

  

}