package kornell.server.scorm.scorm12.rte

import org.infinispan.Cache
import kornell.server.scorm.scorm12.SCORM12PackageManager
import kornell.server.content.ContentManager
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import kornell.server.cdi.Preferred

@ApplicationScoped
class SCORM12PackageManagers @Inject() (
  @Preferred cache: Cache[String, SCORM12PackageManager]) {
  
  def this() = this(null)

  def get(cm: ContentManager): SCORM12PackageManager = {
    val key = cm.getID()
    if (cache.containsKey(key))
      cache.get(key)
    else {
      val value = new SCORM12PackageManager(cm)
      cache.put(key, value)
      value
    }
    
  }
}