package kornell.server.api

import javax.ws.rs.core.Application
import scala.collection.JavaConverters._
import javax.ws.rs.ApplicationPath
import kornell.server.ws.rs.AutoBeanWriter
import org.jboss.resteasy.plugins.providers.DefaultTextPlain
import java.util.Collections
import kornell.server.ws.rs.EventsReader

class KornellAPI extends Application {
  override def getClasses() = Set[Class[_]](
      classOf[AutoBeanWriter],
      classOf[EventsReader],
      
      classOf[AuthResource],
      classOf[RootResource],
      classOf[UserResource],
      classOf[CoursesResource],
      classOf[OptionResource],
      classOf[RegistrationsResource],
      classOf[InstitutionsResource],
      classOf[ReportResource],
      classOf[EnrollmentResource],
      classOf[EventsResource]
  ) asJava
  
  override def getSingletons() = Collections.emptySet()
}