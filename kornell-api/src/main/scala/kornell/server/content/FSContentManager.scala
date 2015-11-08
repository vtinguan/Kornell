package kornell.server.content

import kornell.core.entity.FSContentRepository
import scala.util.Try
import scala.io.Source
import java.nio.file.Paths
import java.io.FileInputStream
import java.io.InputStream
import kornell.core.util.StringUtils
import java.nio.file.Files

class FSContentManager(fsRepo:FSContentRepository) extends SyncContentManager {
	 def source(key: String): Try[Source] = Try {
	   val path = Paths.get(fsRepo.getPath,fsRepo.getPrefix(),key)
	   Source.fromFile(path.toFile())
	 }
	 
	 def inputStream(key: String): Try[InputStream] = Try {
		 new FileInputStream(Paths.get(fsRepo.getPath, key).toFile())
	 }
	 

	 def put(key: String, input: InputStream, contentType: String, contentDisposition: String, metadataMap: Map[String, String]) = 
		 Files.copy(input,Paths.get(key)) 
	 
	 def getPrefix = fsRepo.getPrefix
}