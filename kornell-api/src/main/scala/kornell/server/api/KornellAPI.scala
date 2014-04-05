package kornell.server.api

import java.util.Collections

import scala.collection.JavaConverters.setAsJavaSetConverter

import javax.ws.rs.core.Application
import kornell.server.dev.ProbesResource
import kornell.server.ws.rs.AutoBeanWriter
import kornell.server.ws.rs.TOReader
import kornell.server.ws.rs.exception.NoSuchElementMapper
import kornell.server.ws.rs.reader.EntityReader
import kornell.server.ws.rs.reader.EventsReader
import kornell.server.ws.rs.reader.LOMReader
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
      classOf[PeopleResource],
      classOf[UserResource],
      classOf[CoursesResource],
      classOf[CourseVersionsResource],
      classOf[CourseClassesResource],
      classOf[RegistrationsResource],
      classOf[InstitutionsResource],
      classOf[ReportResource],
      classOf[EnrollmentsResource],
      classOf[EventsResource],
      classOf[RepositoryResource],
      classOf[ActomResource],
      
      classOf[ProbesResource],
      classOf[SandboxResource],
      classOf[HealthCheckResource],
      classOf[NewRelicResource]
  ) asJava
  
  override def getSingletons() = Collections.emptySet()
}