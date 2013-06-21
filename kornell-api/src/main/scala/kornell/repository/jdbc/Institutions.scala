package kornell.repository.jdbc

import kornell.repository.Repository
import kornell.repository.Beans
import kornell.core.shared.data.Institution

object Institutions extends Repository with Beans {
  def create(name: String, terms: String) = """
	      | insert into Institution(uuid,name,terms) 
		  | values (?,?,?)"""
    .executeUpdate(Institution(randomUUID,name,terms))  
}