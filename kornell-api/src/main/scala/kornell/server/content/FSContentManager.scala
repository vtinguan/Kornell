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
	 def source( infix: String, key: String): Try[Source] = Try {
	   val path = Paths.get(fsRepo.getPath,fsRepo.getPrefix(),infix, key)
	   Source.fromFile(path.toFile())
	 }
	 
	 def inputStream(infix: String, key: String): Try[InputStream] = Try {
		 new FileInputStream(Paths.get(fsRepo.getPath ,infix, key).toFile())
	 }
	 
	 def url(segments:String*):String = StringUtils.mkurl(fsRepo.getPrefix(),segments:_*)
	   
	 def put(key: String, input: InputStream, contentType: String, contentDisposition: String, metadataMap: Map[String, String]) = 
		 Files.copy(input,Paths.get(key)) 
	 
	 def getPrefix = fsRepo.getPrefix
}