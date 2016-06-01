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
	 def source(keys: String*): Try[Source] = Try {
	   val path = Paths.get(fsRepo.getPath,url(keys:_*))
	   Source.fromFile(path.toFile(), "UTF-8")
	 }
	 
	 def inputStream(keys: String*): Try[InputStream] = Try {
	   val path = Paths.get(fsRepo.getPath, url(keys:_*))
	   val file = path.toFile()
		 if(file.exists()) new FileInputStream(file) 
		 else throw new IllegalArgumentException(s"$path not found")
	 }

	 def put(input: InputStream, contentType: String, contentDisposition: String, metadataMap: Map[String, String],keys: String*) = 
		 Files.copy(input,Paths.get(url(keys:_*))) 
	 
	 def getPrefix = fsRepo.getPrefix
}