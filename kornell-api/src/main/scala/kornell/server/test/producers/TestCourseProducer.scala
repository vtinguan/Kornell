package kornell.server.test.producers

import javax.enterprise.context.Dependent
import kornell.server.jdbc.repository.CoursesRepo
import javax.inject.Inject
import kornell.server.repository.Entities
import kornell.server.test.util.Generator
import javax.enterprise.inject.Produces
import kornell.core.entity.Institution
import java.util.logging.Logger
import javax.enterprise.context.ApplicationScoped

@Dependent
class TestCourseProducer @Inject() (
  csRepo: CoursesRepo,
  itt: Institution)
  extends Producer {

  @Produces
  @ApplicationScoped
  def course = csRepo.create(uuid = randUUID,
    code = randStr(5),
    institutionUUID = itt.getUUID)
}