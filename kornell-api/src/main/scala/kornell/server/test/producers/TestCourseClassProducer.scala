package kornell.server.test.producers

import javax.enterprise.context.ApplicationScoped
import kornell.server.api.CourseClassResource
import javax.enterprise.inject.Produces
import kornell.server.cdi.EmailCourseClass
import javax.inject.Inject
import kornell.core.entity.CourseClass
import kornell.server.api.CourseClassesResource
import kornell.server.repository.Entities
import kornell.core.entity.CourseVersion
import kornell.core.entity.Institution
import kornell.core.entity.RegistrationEnrollmentType
import javax.enterprise.context.Dependent

@Dependent
class CourseClassProducer @Inject() (
  val ccr: CourseClassesResource,
  val courseVersion: CourseVersion,
  val institution: Institution)
  extends Producer {

  @Produces @EmailCourseClass
  def newCourseClassEmail: CourseClass = ccr.create(
      name = randName ,
      courseVersionUUID = courseVersion.getUUID,
      institutionUUID = institution.getUUID,
      registrationEnrollmentType = RegistrationEnrollmentType.email)
}