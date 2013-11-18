package kornell.server.api

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.server.dev.util.ContentsParser
import kornell.server.repository.jdbc.Courses
import kornell.server.repository.s3.S3
import kornell.core.to.CourseTO
import kornell.core.lom.Contents
import kornell.server.repository.jdbc.Auth
import kornell.core.entity.Person
import kornell.core.lom.Content
 
class CourseResource(uuid: String) {
  @GET
  @Produces(Array(CourseTO.TYPE))
  def getCourse(implicit @Context sc: SecurityContext) =
    Courses.byUUID(uuid)

  @Produces(Array(Contents.TYPE))
  @Path("contents")
  @GET
  def getContents(implicit @Context sc: SecurityContext):Contents = 
  Auth.withPerson { person =>
    val courseRepo = Courses(uuid)
    val courseTO = courseRepo.withEnrollment(person).get    
    val s3 = S3(courseTO.getCourse.getRepositoryUUID)
    val structureSrc = s3.source("structure.knl")    
    val structureText = structureSrc.mkString("")
    val baseURL = s3.baseURL
    val visited =  courseRepo.actomsVisitedBy(person)
    val contents = ContentsParser.parse(baseURL,s3.prefix,structureText,visited)
    contents.setCourseTO(courseTO)
    contents
  }
}

object CourseResource {
  def apply(uuid: String) = new CourseResource(uuid)
}