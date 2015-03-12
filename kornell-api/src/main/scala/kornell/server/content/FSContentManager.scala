package kornell.server.content

import java.io.FileInputStream
import java.nio.file.Paths
import java.io.InputStream
import kornell.core.entity.ContentStore
import kornell.core.util.StringUtils._
import scala.util.Try
import java.util.logging.Logger
import java.io.FileNotFoundException
import java.nio.file.Files

class FSContentManager(cs:ContentStore,distributionPrefix:String) extends ContentManager {
  val log = Logger.getLogger(classOf[FSContentManager].getClass.getName)
  val path = cs.getProperties().get("path")
  
  override def getID() = cs.getUUID()+"/"+distributionPrefix
    
  override def getObjectStream(obj:String):Try[InputStream] = Try {
    log.finest(s"Fetching object [fs://${path}/${distributionPrefix}/${obj}]")
  	val jpath = Paths.get(path, distributionPrefix, obj)
  	if (Files.notExists(jpath))
  	  throw new RuntimeException(s"Can not stream from inexistent path [${path}]")
  	Files.newInputStream(jpath)
  }
  
  override def getURL(obj:String) = composeURL(baseURL,distributionPrefix,obj)
  
  override def baseURL = repoURL
  
  val repoURL = composeURL(s"/repository/${cs.getUUID}")   
}