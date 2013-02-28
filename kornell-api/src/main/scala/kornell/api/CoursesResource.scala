package kornell.api

import javax.ws.rs.Produces
import javax.ws.rs.Path
import scala.collection.JavaConverters._
import javax.ws.rs.GET
import javax.inject.Inject
import javax.persistence.EntityManager
import com.google.web.bindery.autobean.vm.AutoBeanFactorySource
import kornell.core.shared.to.TOFactory
import java.util.UUID
import java.util.Date
import com.google.web.bindery.autobean.shared.AutoBeanCodex
import com.google.web.bindery.autobean.shared.AutoBeanUtils
import kornell.core.shared.to.CoursesTO
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.entity.Enrollment
import kornell.entity.Course
import scala.collection.mutable.MutableList
import kornell.core.shared.to.CourseTO
import java.util.ArrayList
import kornell.dev.Mocks

@Produces(Array("application/vnd.kornell.v1.to.coursesto+json;charset=utf-8"))
@Path("courses")
class CoursesResource @Inject() (
  val em: EntityManager,
  val toFactory: TOFactory) {
  
  def this() = this(null, null)

  @GET
  def getCourses(@Context sc: SecurityContext) = Mocks.courses
}