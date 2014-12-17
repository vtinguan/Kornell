package kornell.server.api

import javax.ws.rs.Produces
import javax.ws.rs.Consumes
import javax.ws.rs.core.SecurityContext
import kornell.core.entity.Person
import javax.ws.rs.PUT
import javax.ws.rs.GET
import kornell.server.jdbc.repository.InstitutionsRepo
import kornell.server.jdbc.repository.AuthRepo
import javax.ws.rs.Path
import javax.ws.rs.core.Context
import kornell.server.jdbc.repository.PersonRepo
import kornell.server.jdbc.repository.PeopleRepo
import javax.ws.rs.QueryParam
import kornell.server.util.Identifiable
import javax.inject.Inject
import kornell.server.jdbc.repository.PersonRepo
import kornell.server.jdbc.repository.PeopleRepo
import kornell.server.jdbc.repository.PersonRepo

@Produces(Array(Person.TYPE))
class PersonResource @Inject()(
    val authRepo:AuthRepo,
    val peopleRepo:PeopleRepo)
    extends Identifiable {
  
  def this() = this(null,null)
  
  @GET
  def get = peopleRepo.byUUID(uuid)

  @Path("isRegistered")
  @Produces(Array("application/boolean"))
  @GET
  def isRegistered(@QueryParam("cpf") cpf:String,
      @QueryParam("email") email:String):Boolean = 
    authRepo.withPerson { person =>
    	val result = get.isRegistered(person.getInstitutionUUID,cpf,email)
    	result
  }
  
}