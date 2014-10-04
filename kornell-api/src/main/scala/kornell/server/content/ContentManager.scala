package kornell.server.content

import kornell.core.entity.ContentStore
import java.io.InputStream
import kornell.core.util.StringUtils

trait ContentManager {
	def getObjectStream(obj:String):InputStream
	def getURL(obj:String): String 
}

object ContentManager {
  def apply(cs:ContentStore):ContentManager = new FSContentManager(cs)
  																						//new S3ContentManager(cs)
}