package kornell.repository.jdbc

import kornell.core.shared.data.Institution
import kornell.repository.Beans
import kornell.repository.Repository

object Institutions extends Repository with Beans {
  def create(name: String, terms: String) = """
    | insert into Institution(uuid,name,terms) 
    | values (?,?,?)"""
    .executeUpdate(Institution(randomUUID, name, terms))
}