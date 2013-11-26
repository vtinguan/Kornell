package kornell.server.api

import javax.ws.rs.Produces
import javax.ws.rs.GET
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import javax.ws.rs.Path
import kornell.core.lom.Contents
import kornell.server.repository.jdbc.Auth
import kornell.server.repository.jdbc.Courses
import kornell.server.repository.s3.S3
import kornell.server.repository.jdbc.CourseClasses
import kornell.server.dev.util.ContentsParser

class CourseClassResource(uuid:String) {
  
  @Produces(Array(Contents.TYPE))
  @Path("contents")
  @GET
  def getLatestContents(implicit @Context sc: SecurityContext): Contents =
    Auth.withPerson { person =>
      val classRepo = CourseClasses(uuid)
      val versionRepo = classRepo.version
      val version = versionRepo.get
      val repositoryUUID = version.getRepositoryUUID();
      val s3 = S3(repositoryUUID)
      val structureSrc = s3.source("structure.knl")
      val structureText = structureSrc.mkString("")
      val baseURL = s3.baseURL
      val visited = classRepo.actomsVisitedBy(person) 
      val contents = ContentsParser.parse(baseURL, s3.prefix, structureText, visited)
      //contents.setCourseClass(classRepo.get)
      contents
    }
}

object CourseClassResource {
  def apply(uuid:String) = new CourseClassResource(uuid)
}