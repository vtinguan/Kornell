package kornell.server.api

import scala.language.implicitConversions
import javax.ws.rs._
import kornell.core.entity.WebRepository
import java.sql.ResultSet
import kornell.server.repository.Entities
import kornell.server.jdbc.SQL._ 

@Path("repository")
@Produces(Array(WebRepository.TYPE))
class RepositoryResource {
  implicit def toWebRepo(rs:ResultSet) = Entities.newWebRepository(
      rs.getString("uuid"), rs.getString("distributionURL"), rs.getString("prefix")) 
  
  //TODO: Cache
  @Path("{uuid}")
  @GET
  def get(@PathParam("uuid") uuid:String) = sql"""
  	select * from S3ContentStore where uuid = $uuid
  """.get[WebRepository]
		  
}