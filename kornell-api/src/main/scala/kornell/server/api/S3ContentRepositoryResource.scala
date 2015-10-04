package kornell.server.api

import kornell.core.entity.S3ContentRepository
import javax.ws.rs.GET
import javax.ws.rs.Produces
import kornell.server.jdbc.repository.S3ContentRepositoryRepo
import kornell.server.util.AccessDeniedErr
import kornell.server.util.Conditional.toConditional
import javax.ws.rs.PUT
import javax.ws.rs.Consumes

class S3ContentRepositoryResource(uuid: String) {

  @GET
  @Produces(Array(S3ContentRepository.TYPE))
  def get = {
    S3ContentRepositoryRepo(uuid).get
  }.requiring(isControlPanelAdmin(), AccessDeniedErr()).get
   
  @PUT
  @Consumes(Array(S3ContentRepository.TYPE))
  @Produces(Array(S3ContentRepository.TYPE))
  def update(contentRepository: S3ContentRepository) = {
    S3ContentRepositoryRepo(uuid).update(contentRepository)
  }.requiring(isControlPanelAdmin(), AccessDeniedErr()).get
  
}