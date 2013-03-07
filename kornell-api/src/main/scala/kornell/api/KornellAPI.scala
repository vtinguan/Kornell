package kornell.api

import javax.ws.rs.core.Application
import scala.collection.JavaConverters._
import javax.ws.rs.ApplicationPath
import kornell.ws.rs.AutoBeanWriter
import org.jboss.resteasy.plugins.providers.DefaultTextPlain

@ApplicationPath("/")
class KornellAPI extends Application {
  override def getClasses() = Set[Class[_]](
      classOf[AuthResource],
      classOf[RootResource],
      classOf[UalaResource],
      classOf[UserResource],
      classOf[CoursesResource],
      classOf[OptionResource],
      classOf[AutoBeanWriter]      
  ) asJava
}