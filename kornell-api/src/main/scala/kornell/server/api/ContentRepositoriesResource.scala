package kornell.server.api

import javax.ws.rs.Path
import javax.ws.rs.POST
import javax.ws.rs.Produces
import javax.ws.rs.Consumes
import kornell.core.entity.S3ContentRepository
import kornell.server.jdbc.repository.ContentRepositoriesRepo
import kornell.server.util.Conditional.toConditional
import kornell.server.util.AccessDeniedErr
import javax.ws.rs.PathParam
import javax.ws.rs.PUT
import kornell.core.entity.FSContentRepository

@Path("contentRepositories")
class ContentRepositoriesResource {
  
  @Path("s3/{uuid}")
  def getS3(@PathParam("uuid") uuid:String):S3ContentRepositoryResource = new S3ContentRepositoryResource(uuid)
  
  @Path("fs/{uuid}")
  def getFS(@PathParam("uuid") uuid:String):FSContentRepositoryResource = new FSContentRepositoryResource(uuid)
  
  @POST
  @Path("s3")
  @Produces(Array(S3ContentRepository.TYPE))
  @Consumes(Array(S3ContentRepository.TYPE))
  def createS3Repo(contentRepository: S3ContentRepository) = {
    ContentRepositoriesRepo.createS3Repo(contentRepository)
  }.requiring(isControlPanelAdmin(), AccessDeniedErr()).get

  @POST
  @Path("fs")
  @Produces(Array(FSContentRepository.TYPE))
  @Consumes(Array(FSContentRepository.TYPE))
  def createFSRepo(contentRepository: FSContentRepository) = {
    ContentRepositoriesRepo.createFSRepo(contentRepository)
  }.requiring(isControlPanelAdmin(), AccessDeniedErr()).get
}