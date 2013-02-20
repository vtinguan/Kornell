package kornell.api

import javax.ws.rs.core.Application
import scala.collection.JavaConverters._
import javax.ws.rs.ApplicationPath
import kornell.ws.rs.AutoBeanWriter

@ApplicationPath("/")
class KornellAPI extends Application {
  override def getClasses() = Set[Class[_]](
      classOf[AuthResource],
      classOf[RootResource],
      classOf[UalaResource],
      classOf[UserResource],
      classOf[CoursesResource],
      classOf[AutoBeanWriter]) asJava
}