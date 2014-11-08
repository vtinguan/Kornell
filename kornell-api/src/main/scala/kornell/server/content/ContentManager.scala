package kornell.server.content

import kornell.core.entity.ContentStore
import java.io.InputStream
import kornell.core.util.StringUtils
import kornell.core.entity.ContentStore
import java.util.logging.Logger
import javax.cache.Caching
import java.io.Serializable
import javax.cache.integration.CacheLoader
import kornell.server.jdbc.repository.ContentStoreRepo
import kornell.core.entity.ContentStoreType._
import java.util.Map
import java.lang.Iterable
import scala.collection.JavaConverters._
import javax.cache._
import javax.cache.configuration._

trait ContentManager extends Serializable {
  def getObjectStream(obj: String): InputStream
  def getURL(obj: String): String
}

object ContentManager {
  private val log = Logger.getLogger(ContentManager.getClass.getName)
  private val cacheMgr = Caching.getCachingProvider.getCacheManager

  def load(contentStoreUUID: String): ContentManager =
    ContentStoreRepo(contentStoreUUID,"NO-ECSISTE")
      .first
      .map { cs =>
        cs.getContentStoreType match {
          case S3 => new S3ContentManager(cs)
          case FS => new FSContentManager(cs)
        }
      }.getOrElse(null)

  val cache: Cache[String, ContentManager] = 
    cacheMgr.createCache("ContentManager",new MutableConfiguration[String, ContentManager]()) 

  def getOrLoad(key: String) = if (cache.containsKey(key)) {
    log.finest(s"Cache Hit for [ContentManager] with key [$key]")
    cache.get(key)
  } else {
    log.finest(s"Cache Miss for [ContentManager] with key [$key]")
    val value = Option(load(key))
    value.foreach {cache.put(key, _)} 
    value.getOrElse(null)
  }

  def apply(cs: ContentStore) = getOrLoad(cs.getUUID())

}
  