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
  def getContents(implicit @Context sc: SecurityContext) = fakeItTillYouMakeIt

  def fakeItTillYouMakeIt(implicit @Context sc: SecurityContext) = {
    val courseTO = Courses.byUUID(uuid).get
    val src = Source.fromFile("/Users/faermanj/Dropbox/craftware/Clientes/MIDWAY/Content/suplementacao-alimentar/v0.2/structure.knl", "utf-8")
    val contents = ContentsParser.parse(src)    
    val s3 = S3(courseTO.getCourse.getRepositoryUUID)
    
    courseTO.setBaseURL(s"http://${s3.bucket}.s3-sa-east-1.amazonaws.com/${s3.prefix}") //TODO: Resolve base url from region
    contents.setCourseTO(courseTO)      
    contents
  }

  def getContentsDraft(implicit @Context sc: SecurityContext) = {
    var result = ""
    val course = Courses.byUUID(uuid).get.getCourse
    val s3 = S3(course.getRepositoryUUID)
    val structure = Option(s3.getObject("structure.knl"))
    if (structure.isDefined) {
      val in = structure.get.getObjectContent()
      result = Source.fromInputStream(in, "utf-8").mkString("")
    }
    result
  }

}

object CourseResource {
  def apply(uuid: String) = new CourseResource(uuid)
}