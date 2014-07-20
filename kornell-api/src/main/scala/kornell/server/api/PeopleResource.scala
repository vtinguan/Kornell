package kornell.server.api

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import kornell.server.jdbc.repository.PeopleRepo
import kornell.core.entity.People
import kornell.server.jdbc.repository.AuthRepo

@Path("people")
@Produces(Array(People.TYPE))
class PeopleResource() {
  
  @Path("{uuid}")
  def get(@PathParam("uuid") uuid:String):PersonResource = new PersonResource(uuid) 
  
  @GET
  def findBySearchTerm(@QueryParam("search") search:String,
       @QueryParam("institutionUUID") institutionUUID:String) = PeopleRepo.findBySearchTerm(search, institutionUUID)

  @Path("isRegistered")
  @Produces(Array("application/boolean"))
  @GET
  def isRegistered(@QueryParam("cpf") cpf:String,
      @QueryParam("email") email:String):Boolean = 
    AuthRepo().withPerson { person =>
    	val result = PeopleRepo.isRegistered(person.getUUID,cpf,email)
    	result
  }
}

object PeopleResource{
  def apply() = new PeopleResource()
}