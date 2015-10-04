package kornell.server.api

import javax.ws.rs.Path
import javax.ws.rs.POST
import javax.ws.rs.Produces
import javax.ws.rs.Consumes
import kornell.core.entity.S3ContentRepository
import kornell.server.jdbc.repository.RepositoriesRepo
import kornell.server.util.Conditional.toConditional
import kornell.server.util.AccessDeniedErr
import javax.ws.rs.PathParam

@Path("contentRepositories")
class S3ContentRepositoriesResource {
  
  @Path("{uuid}")
  def get(@PathParam("uuid") uuid:String):S3ContentRepositoryResource = new S3ContentRepositoryResource(uuid)
  
  @POST
  @Produces(Array(S3ContentRepository.TYPE))
  @Consumes(Array(S3ContentRepository.TYPE))
  def create(contentRepository: S3ContentRepository) = {
    RepositoriesRepo().create(contentRepository)
  }.requiring(isControlPanelAdmin(), AccessDeniedErr()).get

}