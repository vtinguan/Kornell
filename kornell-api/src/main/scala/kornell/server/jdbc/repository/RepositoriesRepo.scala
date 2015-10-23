package kornell.server.jdbc.repository

import kornell.core.entity.S3ContentRepository
import kornell.server.jdbc.SQL._
import kornell.server.repository.Entities
import kornell.server.repository.ContentRepository
import kornell.core.entity.ContentRepository
import java.sql.ResultSet

class RepositoriesRepo {
	def createS3Repository(accessKeyId:String, secretAccessKey:String, bucketName:String, uuid:String = randomUUID, institutionUUID: String, region: String):S3ContentRepository = 
			createS3Repo(Entities.newS3ContentRepository(uuid = uuid, accessKeyId = accessKeyId, secretAccessKey = secretAccessKey, bucketName = bucketName, institutionUUID = institutionUUID, region = region))
	
	def createS3Repo(s3repo : S3ContentRepository): S3ContentRepository = {
	  sql"""
		    | insert into S3ContentRepository (uuid,accessKeyId,secretAccessKey,bucketName,prefix,region,distributionURL, institutionUUID) 
		    | values(
		    | ${s3repo.getUUID},
		    | ${s3repo.getAccessKeyId},
		    | ${s3repo.getSecretAccessKey},
		    | ${s3repo.getBucketName}, 
		    | ${s3repo.getPrefix},
		    | ${s3repo.getRegion},
		    | ${s3repo.getInstitutionUUID})""".executeUpdate
		s3repo
	}
	
	def first(repoUUID:String):Option[ContentRepository] = sql"""
		select repositoryType from ContentRepository  where uuid=$repoUUID
	""".first[String]
	   .flatMap { _ match {
	     case "S3" => firstS3Repository(repoUUID)
	     case _ => throw new IllegalStateException("Unknown repositoryType")
	   	} 
	  }
	
	def firstS3Repository(repoUUID:String) = sql"""
			select * from S3ContentRepository where uuid=$repoUUID
	""".first[S3ContentRepository]
	
	
	implicit def toS3ContentRepository(rs:ResultSet):S3ContentRepository = Entities.newS3ContentRepository(
			rs.getString("uuid"),
			rs.getString("accessKeyId"),
			rs.getString("secretAccessKey"),
			rs.getString("bucketName"),
			rs.getString("prefix"),
			rs.getString("region"),
			rs.getString("institutionUUID"))   
} 

object RepositoriesRepo {
  def apply() = new RepositoriesRepo()
}