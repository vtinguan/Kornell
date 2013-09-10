package kornell.server.util

import scala.io.Source
import java.io.ByteArrayInputStream
import java.nio.file.Files
import java.io.ByteArrayOutputStream
import org.apache.commons.io.IOUtils
import java.net.URLConnection

object DataURI {

  def fromResource(resource: String):Option[String] = {
    val mimeType = URLConnection.guessContentTypeFromName(resource);
    val url = getClass.getClassLoader.getResource(resource)    
    val in = url.openStream()
    val out = new ByteArrayOutputStream
    try {
      IOUtils.copy(in, out)
      val bytes = out.toByteArray
      val chars = Base64.encode(bytes) 
      return Some( "data:" + mimeType + ";base64," + chars )      
    } finally {
      IOUtils.closeQuietly(in)
      IOUtils.closeQuietly(out)
    }
    return None
  }
}