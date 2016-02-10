package kornell.server.api

import javax.ws.rs.GET
import javax.ws.rs.Produces
import kornell.server.util.AccessDeniedErr
import kornell.server.util.Conditional.toConditional
import javax.ws.rs.PUT
import javax.ws.rs.Consumes
import kornell.server.jdbc.repository.ContentRepositoriesRepo
import kornell.core.entity.FSContentRepository

class FSContentRepositoryResource(uuid: String) {
  
  @GET
  @Produces(Array(FSContentRepository.TYPE))
  def get = {
    ContentRepositoriesRepo.firstFSRepository(uuid).get
  }.requiring(isControlPanelAdmin(), AccessDeniedErr()).get
   
  @PUT
  @Consumes(Array(FSContentRepository.TYPE))
  @Produces(Array(FSContentRepository.TYPE))
  def update(contentRepository: FSContentRepository) = {
    ContentRepositoriesRepo.updateFSRepo(contentRepository)
  }.requiring(isControlPanelAdmin(), AccessDeniedErr()).get

}