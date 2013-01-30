package kornell.api

import javax.ws.rs.Produces
import javax.ws.rs.Path
import scala.collection.JavaConverters._
import javax.ws.rs.GET
import javax.inject.Inject
import javax.persistence.EntityManager

@Produces(Array("application/vnd.kornell.v1.course+json"))
@Path("courses")
class CoursesResource @Inject() (val em:EntityManager) {
    def this() = this(null)
	
	@GET
	def getCourses = 
	  em.createQuery("select c from Course c").getResultList

}