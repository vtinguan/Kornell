package kornell.server.api

import javax.ws.rs.Produces
import javax.ws.rs.core.SecurityContext
import javax.ws.rs.GET
import kornell.server.jdbc.repository.AuthRepo
import javax.ws.rs.Path
import javax.ws.rs.core.Context
import kornell.core.lom.Contents
import kornell.core.to.CourseClassTO
import kornell.server.dev.util.ContentsParser
import kornell.server.repository.s3.S3
import kornell.server.jdbc.SQL._
import kornell.server.jdbc.repository.CourseClassesRepo

@Path("courseClass")
class CourseClassResource(uuid: String) {

  @GET
  @Path("to")
  @Produces(Array(CourseClassTO.TYPE))
  def get(implicit @Context sc: SecurityContext) =
    AuthRepo.withPerson { person =>
      //CourseClasses(uuid).byPerson(person.getUUID)
    }

  @Produces(Array(Contents.TYPE))
  @Path("contents")
  @GET
  def getLatestContents(implicit @Context sc: SecurityContext): Contents =
    AuthRepo.withPerson { person =>
      val classRepo = CourseClassesRepo(uuid)
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