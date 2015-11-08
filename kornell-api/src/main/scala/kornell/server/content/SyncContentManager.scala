package kornell.server.content

import scala.io.Source
import scala.util.Try
import java.io.InputStream
import kornell.core.util.StringUtils._

trait SyncContentManager { //TODO: Consider Future[T] instead of Try[T]
	 def source(key: String): Try[Source] 
	 def inputStream(key: String): Try[InputStream]
	 def put(key: String, value: InputStream, contentType: String, contentDisposition: String, metadataMap: Map[String, String])
	 
	 //TODO: Consider urls generated on the client side and remove this method
	 def getPrefix():String	 
	 def url(segments:String*):String = mkurl(getPrefix,segments:_*)
}