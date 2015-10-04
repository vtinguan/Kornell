package kornell.server.jdbc.repository

import kornell.server.jdbc.SQL._
import kornell.core.entity.S3ContentRepository


class S3ContentRepositoryRepo(uuid: String) {
  
  def get = sql"""select * from S3ContentRepository where uuid = ${uuid}""".get[S3ContentRepository]

  def update(s3repo : S3ContentRepository): S3ContentRepository = {
	  sql"""
		    | update S3ContentRepository set
		    | accessKeyId = ${s3repo.getAccessKeyId},
		    | secretAccessKey = ${s3repo.getSecretAccessKey},
		    | bucketName = ${s3repo.getBucketName}, 
		    | prefix = ${s3repo.getPrefix},
		    | region = ${s3repo.getRegion},
		    | institutionUUID = ${s3repo.getInstitutionUUID}
		    | where uuid = ${uuid}""".executeUpdate
		s3repo
	}
}

object S3ContentRepositoryRepo {
  def apply(uuid: String) = new S3ContentRepositoryRepo(uuid)
}