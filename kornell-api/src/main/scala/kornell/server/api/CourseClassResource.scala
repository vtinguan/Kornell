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
import javax.servlet.http.HttpServletRequest
import kornell.core.entity.CourseClass
import javax.ws.rs.PUT
import javax.ws.rs.Consumes
import kornell.server.jdbc.repository.CourseClassRepo

@Path("courseClass")
class CourseClassResource(uuid: String) {

  @GET
  @Path("to")
  @Produces(Array(CourseClassTO.TYPE))
  def get(implicit @Context sc: SecurityContext) =
    AuthRepo.withPerson { person =>
      //CourseClasses(uuid).byPerson(person.getUUID)
    }
  
  @PUT
  @Produces(Array("text/plain"))
  @Consumes(Array(CourseClass.TYPE))
  def update(implicit @Context sc: SecurityContext, courseClass: CourseClass) = AuthRepo.withPerson{ p =>
    CourseClassRepo(uuid).update(courseClass)
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
      val structureSrc = repo.source(version.getDistributionPrefix(),"structure.knl")
      val structureText = structureSrc.mkString("")
      val baseURL = repo.baseURL
      val visited = classRepo.actomsVisitedBy(person)
      val contents = ContentsParser.parse(baseURL, repo.prefix + "/" +version.getDistributionPrefix() , structureText, visited)

      //contents.setCourseClass(classRepo.get)
      contents
    }

}

object CourseClassResource {
  def apply(uuid: String) = new CourseClassResource(uuid)
}