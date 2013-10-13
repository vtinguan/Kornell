package kornell.server.api

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.server.dev.util.ContentsParser
import kornell.server.repository.jdbc.Courses
import kornell.server.repository.s3.S3
import kornell.core.shared.to.CourseTO
import kornell.core.shared.data.Contents

class CourseResource(uuid: String) {
  @GET
  @Produces(Array(CourseTO.TYPE))
  def getCourse(implicit @Context sc: SecurityContext) =
    Courses.byUUID(uuid)

  @Produces(Array(Contents.TYPE))
  @Path("contents")
  @GET
  def getContents(implicit @Context sc: SecurityContext) = {
    val courseTO = Courses.byUUID(uuid).get    
    val s3 = S3(courseTO.getCourse.getRepositoryUUID)
    val structureSrc = s3.source("structure.knl")    
    val  structureText = structureSrc.mkString("")      
    courseTO.setBaseURL(s3.baseURL)
    val contents = ContentsParser.parse(structureText)
    contents.setCourseTO(courseTO)      
    contents   
  }
}

object CourseResource {
  def apply(uuid: String) = new CourseResource(uuid)
}