package kornell.server.jdbc.repository

import kornell.core.entity.S3ContentRepository
import kornell.server.jdbc.SQL._
import kornell.server.repository.Entities
import kornell.server.repository.ContentRepository
import kornell.core.entity.ContentRepository
import java.sql.ResultSet
import kornell.core.entity.FSContentRepository

class RepositoriesRepo {
	def createS3Repository(accessKeyId:String, secretAccessKey:String, bucketName:String, uuid:String = randomUUID, institutionUUID: String, region: String):S3ContentRepository = 
			createS3Repo(Entities.newS3ContentRepository(uuid = uuid, accessKeyId = accessKeyId, secretAccessKey = secretAccessKey, bucketName = bucketName, institutionUUID = institutionUUID, region = region))
			
	def createFSRepository(uuid:String = randomUUID, path: String, prefix: String):FSContentRepository = 
			createFSRepo(Entities.newFSContentRepository(uuid = uuid, path = path, prefix = prefix))
	
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
	
	def createFSRepo(fsRepo : FSContentRepository): FSContentRepository = {
	  sql"""
		    | insert into FSContentRepository (uuid,path,prefix) 
		    | values(
		    | ${fsRepo.getUUID},
		    | ${fsRepo.getPath},
		    | ${fsRepo.getPrefix})""".executeUpdate
	    fsRepo
	}
	
	def updateS3Repo(s3repo : S3ContentRepository): S3ContentRepository = {
	  sql"""
		    | update S3ContentRepository set
		    | accessKeyId = ${s3repo.getAccessKeyId},
		    | secretAccessKey = ${s3repo.getSecretAccessKey},
		    | bucketName = ${s3repo.getBucketName}, 
		    | prefix = ${s3repo.getPrefix},
		    | region = ${s3repo.getRegion},
		    | institutionUUID = ${s3repo.getInstitutionUUID}
		    | where uuid = ${s3repo.getUUID}""".executeUpdate
		s3repo
	}
	
	def updateFSRepo(fsRepo: FSContentRepository): FSContentRepository = {
	  sql"""
		    | update FSContentRepository set
		    | path = ${fsRepo.getPath},
		    | prefix = ${fsRepo.getPrefix}
		    | where uuid = ${fsRepo.getUUID}""".executeUpdate
	    fsRepo
	}
	
	def first(repoUUID:String):Option[ContentRepository] = sql"""
		select repositoryType from ContentRepository  where uuid=$repoUUID
	""".first[String]
	   .flatMap { _ match {
	     case "S3" => firstS3Repository(repoUUID)
	     case "FS" => firstFSRepository(repoUUID)
	     case _ => throw new IllegalStateException("Unknown repositoryType")
	   	} 
	  }
	
	def firstS3Repository(repoUUID:String) = sql"""
		select * from S3ContentRepository where uuid=$repoUUID
	""".first[S3ContentRepository]
	
	def firstFSRepository(repoUUID:String) = sql"""
		select * from FSContentRepository where uuid=$repoUUID
	""".first[FSContentRepository]
	
	implicit def toFSContentRepository(rs:ResultSet):FSContentRepository = Entities.newFSContentRepository(
			rs.getString("uuid"),
			rs.getString("path"),
			rs.getString("prefix")
	)
	
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