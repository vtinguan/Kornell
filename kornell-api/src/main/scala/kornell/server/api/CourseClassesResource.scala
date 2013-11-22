package kornell.server.api
import javax.ws.rs.Path

@Path("classes")
class CourseClassesResource {
  @Path("{uuid}")
  def getCourseClassResource(uuid:String) = CourseClassResource(uuid)
}