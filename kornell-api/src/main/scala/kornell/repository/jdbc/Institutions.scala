package kornell.repository.jdbc

import kornell.repository.Repository
import kornell.repository.Beans

object Institutions extends Repository with Beans {
	def create(name:String,terms:String) = {
	  val i = newInstitution(name = name, terms=terms)
	  update("""
	      | insert into Institution(uuid,name,terms) 
		  | values (?,?,?)"""
	  )(i.getUUID,i.getName,i.getTerms)
	}
}