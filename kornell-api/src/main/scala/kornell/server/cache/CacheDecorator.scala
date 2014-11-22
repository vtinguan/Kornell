package kornell.server.cache

import javax.cache._
import javax.cache.integration.CacheLoader
import java.util.logging.Logger

class CacheDecorator[E,R <: CacheLoader[String,E]](decorated:R) {
  
  val log = Logger.getLogger(CacheDecorator.this.getClass.getName)
  val cacheName = decorated.getClass.getName
  def cache: Cache[String, E] = CacheDecorator.cacheMgr.getCache(cacheName)

  def load(key: String): E = if (cache.containsKey(key)) {
    log.finest(s"Cache Hit on [cache:$cacheName] for key [$key]")
    cache.get(key)
  } else {
    log.finest(s"Cache Miss on [cache:$cacheName] for key [$key]")
    val e: E = decorated.load(key)
    cache.put(key, e)
    e
  }
}

object CacheDecorator {
  private val cacheMgr = Caching.getCachingProvider.getCacheManager
}