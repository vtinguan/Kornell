package kornell.server.repository.service

import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.amazonaws.HttpMethod
import org.joda.time.DateTime
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.auth.BasicAWSCredentials
import kornell.core.entity.S3ContentRepository
import kornell.core.util.StringUtils._
import kornell.server.jdbc.repository.CourseVersionRepo
import kornell.server.jdbc.repository.CourseClassRepo
import kornell.server.jdbc.repository.CourseRepo
import kornell.server.jdbc.repository.InstitutionRepo
import kornell.server.content.ContentManagers
import kornell.server.jdbc.repository.ContentRepositoriesRepo
import java.util.Date

object CourseVersionUploadService {
  
  def getUploadUrl(courseVersionUUID: String) = {
    val courseVersion = CourseVersionRepo(courseVersionUUID).get
    val institution = InstitutionRepo(CourseRepo(courseVersion.getCourseUUID).get.getInstitutionUUID).get
    val repo = ContentRepositoriesRepo.firstS3Repository(institution.getAssetsRepositoryUUID).get
    
    val s3 = if (isSome(repo.getAccessKeyId()))
      new AmazonS3Client(new BasicAWSCredentials(repo.getAccessKeyId(),repo.getSecretAccessKey()))
    else  
      new AmazonS3Client
      
    
    val path = "repository/" + repo.getUUID + "/" + courseVersion.getDistributionPrefix + "upload" + new Date().getTime + ".zip";
    val presignedRequest = new GeneratePresignedUrlRequest(repo.getBucketName, path)
    presignedRequest.setMethod(HttpMethod.PUT)
    presignedRequest.setExpiration(new DateTime().plusMinutes(1).toDate)
    presignedRequest.setContentType("application/zip")
    s3.generatePresignedUrl(presignedRequest).toString
  }
}