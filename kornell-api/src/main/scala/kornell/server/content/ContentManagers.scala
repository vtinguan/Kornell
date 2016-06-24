package kornell.server.content

import kornell.server.jdbc.SQL._
import com.amazonaws.services.s3.AmazonS3Client
import kornell.server.jdbc.repository.ContentRepositoriesRepo
import kornell.core.entity.S3ContentRepository
import kornell.server.util.Settings
import kornell.server.repository.Entities
import kornell.core.entity.FSContentRepository
import kornell.server.util.Settings._
import kornell.core.util.StringUtils.mkurl
object ContentManagers {
  
  lazy val s3 = new AmazonS3Client

  def forRepository(repoUUID: String): SyncContentManager = 
    ContentRepositoriesRepo
    	.getByRepositoryUUID(repoUUID)
    	.map {	_ match {
    	  case s3repo:S3ContentRepository => new S3ContentManager(s3repo)
    	  case fsRepo:FSContentRepository => new FSContentManager(fsRepo)
    	  case _ => throw new IllegalStateException("Unknow repository type")
    	}
  	}.getOrElse(throw new IllegalArgumentException(s"Could not find repository [$repoUUID]"))
  
  
  lazy val USER_CONTENT_URL = mkurl("https://s3.amazonaws.com", USER_CONTENT_BUCKET, "") 
  
  def certsRepo(institutionUUID:String) = 
    Entities.newS3ContentRepository(null , null, null, USER_CONTENT_BUCKET, "usercontent/certificates", USER_CONTENT_REGION, institutionUUID)
  
  def forCertificates(institutionUUID:String): SyncContentManager = new S3ContentManager(certsRepo(institutionUUID))
  
}