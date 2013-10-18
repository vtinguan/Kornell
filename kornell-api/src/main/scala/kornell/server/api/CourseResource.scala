package kornell.server.api

import scala.collection.JavaConverters._
import javax.ws.rs._
import javax.ws.rs.core._
import kornell.core.shared.data._
import kornell.core.shared.to.CourseTO
import kornell.core.shared.to.CourseTO
import kornell.server.repository.TOs
import kornell.server.repository.TOs
import kornell.server.repository.jdbc.Courses
import kornell.server.repository.jdbc.Courses
import kornell.server.repository.s3.S3
import scala.io.Source
import kornell.server.dev.util.ContentsParser
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
    val structureText = structureSrc.mkString("")
    val baseURL = s3.baseURL
    courseTO.setBaseURL(baseURL)
    val contents = ContentsParser.parse(baseURL,structureText)
    contents.setCourseTO(courseTO)      
    contents   
  }
}

object CourseResource {
  def apply(uuid: String) = new CourseResource(uuid)
}