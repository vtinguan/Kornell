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
  val course:Course) 
  extends Producer {
  
  @Produces
  def courseVersion = ccrsRepo.create(repositoryUUID = randUUID, 
      courseUUID = course.getUUID)
  
}