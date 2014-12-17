package kornell.server.jdbc.repository

import kornell.core.entity.S3ContentRepository
import kornell.server.jdbc.SQL._
import kornell.server.repository.Entities

class RepositoriesRepo {
	def createS3Repository(accessKeyId:String, secretAccessKey:String, bucketName:String, uuid:String = randomUUID, institutionUUID: String, region: String):S3ContentRepository = 
			create(Entities.newS3ContentRepository(uuid = uuid, accessKeyId = accessKeyId, secretAccessKey = secretAccessKey, bucketName = bucketName, institutionUUID = institutionUUID, region = region))
	
	def create (s3repo : S3ContentRepository): S3ContentRepository = {
	  sql"""
		    | insert into S3ContentRepository (uuid,accessKeyId,secretAccessKey,bucketName,prefix,region,distributionURL, institutionUUID) 
		    | values(
		    | ${s3repo.getUUID},
		    | ${s3repo.getAccessKeyId},
		    | ${s3repo.getSecretAccessKey},
		    | ${s3repo.getBucketName}, 
		    | ${s3repo.getPrefix},
		    | ${s3repo.getRegion},
		    | ${s3repo.getDistributionURL},
		    | ${s3repo.getInstitutionUUID})""".executeUpdate
		s3repo
	}
} 

object RepositoriesRepo {
  def apply() = new RepositoriesRepo()
}