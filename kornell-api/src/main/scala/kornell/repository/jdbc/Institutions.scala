package kornell.repository.jdbc

import kornell.core.shared.data.Institution
import kornell.repository.Beans
import kornell.repository.SlickRepository
import kornell.core.shared.data.Institution
import kornell.core.shared.data.Person
import kornell.core.shared.data.Registration
import kornell.repository.JDBCRepository

object Institutions extends JDBCRepository with Beans {
  implicit def dehydrate(i: Institution) = List(i.getUUID, i.getName, i.getTerms)
  implicit def hydrate(l: List[String]): Institution = Institution(l(0), l(1), l(2))

  def create(name: String, terms: String): Institution = {
    val i = Institution(randomUUID, name, terms)
    """
    | insert into Institution(uuid,name,terms) 
    | values (?,?,?)""".executeUpdate(i)
    i
  }

  def register(p: Person, i: Institution): Registration = {
    val r = Registration(p, i)
    """
    | insert into Registration(person_uuid,institution_uuid)
    | values (?,?)
    """.executeUpdate(List(p.getUUID, i.getUUID))
    r
  }

}