package kornell.server.content

import java.io.InputStream
import java.io.Serializable
import java.util.logging.Logger

import scala.io.Source

import org.infinispan.Cache

import javax.inject.Inject
import kornell.core.entity.ContentStore
import kornell.core.entity.ContentStoreType.FS
import kornell.core.entity.ContentStoreType.S3
import kornell.core.entity.CourseVersion
import kornell.server.cdi.ContentManagerCache
import kornell.server.jdbc.repository.ContentStoreRepo

trait ContentManager extends Serializable {  
  def getObjectStream(key: String): InputStream
  def getURL(key: String): String  
  def getID():String
  def source(key:String):Source = Source.fromInputStream(getObjectStream(key),"UTF-8")
  def baseURL:String
}

class ContentManagers @Inject()(
    @ContentManagerCache cache:Cache[String,ContentManager],
    contentStoreRepo:ContentStoreRepo
    ) {
  private val log = Logger.getLogger("ContentManagers")  

  def load(contentStoreUUID: String,distributionPrefix:String): ContentManager =
    contentStoreRepo
      .first(contentStoreUUID) 
      .map { cs =>
        cs.getContentStoreType match {
          case S3 => new S3ContentManager(cs,distributionPrefix)
          case FS => new FSContentManager(cs,distributionPrefix)
        }
      }.getOrElse(null)

  def getOrLoad(key: String,distributionPrefix:String) = if (cache.containsKey(key)) {
    log.finest(s"Cache Hit for [ContentManager] with key [$key]")
    cache.get(key)
  } else {
    log.finest(s"Cache Miss for [ContentManager] with key [$key]")
    val value = Option(load(key,distributionPrefix))
    value.foreach {cache.put(key, _)} 
    value.getOrElse(null)
  }

  def get(cs: ContentStore,distributionPrefix:String) = getOrLoad(cs.getUUID,distributionPrefix)

  def forCourseVersion(cv:CourseVersion) = {
	  val store = contentStoreRepo.get(cv.getRepositoryUUID)
	  get(store,cv.getDistributionPrefix())
  }
  
  
}
  