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
import kornell.core.to.CourseClassesTO
import javax.ws.rs.PathParam
import kornell.server.repository.jdbc.SQLInterpolation.SQLHelper
import kornell.core.to.CourseTO
import kornell.core.to.CourseClassTO

@Path("courseClass")
class CourseClassResource(uuid: String) {

  @GET
  @Path("to")
  @Produces(Array(CourseClassTO.TYPE))
  def get(implicit @Context sc: SecurityContext) =
    Auth.withPerson { person =>
      //CourseClasses(uuid).byPerson(person.getUUID)
    }

  @Produces(Array(Contents.TYPE))
  @Path("contents")
  @GET
  def getLatestContents(implicit @Context sc: SecurityContext): Contents =
    Auth.withPerson { person =>
      val classRepo = CourseClasses(uuid)
      val versionRepo = classRepo.version
      val version = versionRepo.get
      val repositoryUUID = version.getRepositoryUUID();
      val repo = S3(repositoryUUID)
      /*val contents = if (repo.exists("imsmanifest.xml")) {
        null
      }else{ 
      }*/
      val structureSrc = repo.source("structure.knl")
      val structureText = structureSrc.mkString("")
      val baseURL = repo.baseURL
      val visited = classRepo.actomsVisitedBy(person)
      val contents = ContentsParser.parse(baseURL, repo.prefix, structureText, visited)

      //contents.setCourseClass(classRepo.get)
      contents
    }

  	
}

object CourseClassResource {
  def apply(uuid: String) = new CourseClassResource(uuid)
}