package kornell.server.cdi

import javax.inject.Provider
import org.infinispan.Cache
import kornell.server.content.ContentManager
import org.infinispan.manager.DefaultCacheManager
import org.infinispan.configuration.global.GlobalConfigurationBuilder
import org.infinispan.configuration.cache.ConfigurationBuilder
import org.infinispan.configuration.cache.CacheMode
import org.infinispan.eviction.EvictionStrategy
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Produces
import java.util.logging.Logger
import javax.inject.Inject
import org.infinispan.configuration.global.GlobalConfiguration
import javax.enterprise.inject.Disposes
import kornell.server.scorm.scorm12.SCORM12PackageManager

@ApplicationScoped
class CacheProducer {
  val log = Logger.getLogger(classOf[CacheProducer].getName)
  
  @Produces 
  @ApplicationScoped
  val gc =  new GlobalConfigurationBuilder()
      .globalJmxStatistics()
      .disable()
      .cacheManagerName("KornellCache")
      .build
  
  @Produces 
  @ApplicationScoped 
  @LocalCacheManager
  val localCM: DefaultCacheManager = {
    log.finer("Producing LocalCacheManager")
    val cfg = new ConfigurationBuilder()
      .jmxStatistics().disable()
      .clustering().cacheMode(CacheMode.LOCAL)
      .eviction().strategy(EvictionStrategy.LIRS).maxEntries(1000)
      .build()
    new DefaultCacheManager(gc,cfg) 
  }
  
  def dispose(@Disposes @LocalCacheManager cm:DefaultCacheManager) = {
    log.finer("Disposing LocalCacheManager")
    cm.stop
  }
  
  @Produces 
  @ContentManagerCache
  @ApplicationScoped
  def createContentManagerCache(): Cache[String, ContentManager] = {
    log.finer("Producing Cache[ContentManager]")   
    localCM.getCache("content-managers-cache")
  }
  
  @Produces
  @Preferred
  @ApplicationScoped
  def createSCORM12PackageManagerCache(): Cache[String, SCORM12PackageManager] = {
    log.finer("Producing Cache[SCORM12PackageManager]")   
    localCM.getCache("package-managers-cache")
  }
}