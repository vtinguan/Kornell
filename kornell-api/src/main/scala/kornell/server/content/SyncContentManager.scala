package kornell.server.content

import scala.io.Source
import scala.util.Try
import java.io.InputStream

trait SyncContentManager {
	 def source( infix: String, key: String): Try[Source]
	 def inputStream(infix: String, key: String): Try[InputStream]
	 def url(segments:String*):String
	 def put(key: String, value: InputStream, contentType: String, contentDisposition: String, metadataMap: Map[String, String])
}