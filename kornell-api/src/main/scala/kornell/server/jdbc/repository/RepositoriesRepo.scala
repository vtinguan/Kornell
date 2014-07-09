package kornell.server.jdbc.repository

import kornell.core.entity.S3ContentRepository
import kornell.server.jdbc.SQL._
import kornell.server.repository.Entities

class RepositoriesRepo {
	def createS3Repository(uuid:String = randomUUID):S3ContentRepository = {
			val s3repo = Entities.newS3ContentRepository(uuid = uuid)
			s3repo
	}
} 

object RepositoriesRepo {
  def apply() = new RepositoriesRepo()
}