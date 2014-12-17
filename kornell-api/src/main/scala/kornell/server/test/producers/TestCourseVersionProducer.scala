package kornell.server.test.producers

import javax.enterprise.context.ApplicationScoped
import javax.enterprise.context.Dependent
import kornell.server.jdbc.repository.CourseVersionsRepo
import kornell.server.repository.Entities
import javax.inject.Inject
import kornell.core.entity.Course
import javax.enterprise.inject.Produces

@Dependent
class TestCourseVersionProducer @Inject()(
  val ccrsRepo: CourseVersionsRepo,
  val course:Course) {
  
  @Produces
  def courseVersion = {
    val repositoryUUID: String = "does-not-exit-actyallu"
    // RepositoriesRepo().createS3Repository("", "", "", institutionUUID = institutionUUID, region = "sa-east-1").getUUID
    ccrsRepo.create(Entities.newCourseVersion(repositoryUUID = repositoryUUID, courseUUID = course.getUUID()))
  }
}