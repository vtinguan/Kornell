package kornell.server.api

import javax.ws.rs.core.Application
import scala.collection.JavaConverters._
import javax.ws.rs.ApplicationPath
import kornell.server.ws.rs.AutoBeanWriter
import org.jboss.resteasy.plugins.providers.DefaultTextPlain
import java.util.Collections
import kornell.server.ws.rs.reader.EventsReader
import kornell.server.ws.rs.reader.LOMReader
import kornell.server.ws.rs.reader.EntityReader
import kornell.server.ws.rs.TOReader
import kornell.server.dev.ProbesResource
import kornell.server.ws.rs.exception.NoSuchElementMapper
import kornell.server.ws.rs.writer.BooleanWriter

class KornellAPI extends Application {
  override def getClasses() = Set[Class[_]](
      classOf[AutoBeanWriter],
      classOf[EventsReader],
      classOf[TOReader],
      classOf[EntityReader],
      classOf[LOMReader],
      classOf[NoSuchElementMapper],
      classOf[BooleanWriter],
      
      classOf[RootResource],
      classOf[UserResource],
      classOf[CoursesResource],
      classOf[CourseClassesResource],
      classOf[RegistrationsResource],
      classOf[InstitutionsResource],
      classOf[ReportResource],
      classOf[EnrollmentsResource],
      classOf[EventsResource],
      classOf[S3Resource],
      classOf[RepositoryResource],
      classOf[ActomResource],
      
      classOf[ProbesResource],
      classOf[SandboxResource]
  ) asJava
  
  override def getSingletons() = Collections.emptySet()
}