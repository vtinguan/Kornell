package kornell.server.api

import kornell.core.entity.S3ContentRepository
import javax.ws.rs.GET
import javax.ws.rs.Produces

import kornell.server.util.AccessDeniedErr
import kornell.server.util.Conditional.toConditional
import javax.ws.rs.PUT
import javax.ws.rs.Consumes
import kornell.server.jdbc.repository.ContentRepositoriesRepo

class S3ContentRepositoryResource(uuid: String) {

  @GET
  @Produces(Array(S3ContentRepository.TYPE))
  def get = {
    ContentRepositoriesRepo.firstS3Repository(uuid).get
  }.requiring(isControlPanelAdmin(), AccessDeniedErr()).get
   
  @PUT
  @Consumes(Array(S3ContentRepository.TYPE))
  @Produces(Array(S3ContentRepository.TYPE))
  def update(contentRepository: S3ContentRepository) = {
    ContentRepositoriesRepo.updateS3Repo(contentRepository)
  }.requiring(isControlPanelAdmin(), AccessDeniedErr()).get
  
}