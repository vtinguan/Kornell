package kornell.server.content

import kornell.core.entity.ContentStore
import java.io.InputStream

trait ContentManager {
	def getObjectStream(obj:String):InputStream
	def getPath(obj:String):String
}

object ContentManager {
  def apply(cs:ContentStore):ContentManager = new S3ContentManager(cs)
}