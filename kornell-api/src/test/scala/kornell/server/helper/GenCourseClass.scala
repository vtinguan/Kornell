package kornell.server.helper

import kornell.server.repository.Entities
import java.util.Date
import kornell.core.entity.CourseClassState
import kornell.server.api.CoursesResource
import kornell.server.api.CourseVersionsResource
import kornell.server.api.CourseClassesResource
import kornell.core.entity.CourseClass
import kornell.server.api.RepositoryResource
import kornell.core.entity.RegistrationEnrollmentType
import kornell.server.jdbc.repository.CourseVersionsRepo
import kornell.server.jdbc.repository.CoursesRepo
import kornell.server.test.util.Generator


trait GenCourseClass
	extends GenInstitution
	with Generator {
  /*
  val classCode = randStr(5)
  
  val course = ???//CoursesRepo.create(Entities.newCourse(randUUID, classCode, null, null, null))
  val courseUUID = ???//course.getUUID
  
  val courseVersion = {
    val repositoryUUID :String = ??? // RepositoriesRepo().createS3Repository("", "", "", institutionUUID = institutionUUID, region = "sa-east-1").getUUID
   // CourseVersionsRepo.create(Entities.newCourseVersion(repositoryUUID=repositoryUUID, courseUUID = courseUUID))
  ???
  }
  val courseVersionUUID = ??? //courseVersion.getUUID
  val ccr:CourseClassesResource = ???
  def newPublicCourseClass = ccr.create((Entities.newCourseClass(
    courseVersionUUID="",
    institutionUUID=institutionUUID,
    registrationEnrollmentType=RegistrationEnrollmentType.email,
    publicClass=true)))
        
  def newCourseClassCpf:CourseClass = ccr.create((Entities.newCourseClass(
    name=randStr(5),
    courseVersionUUID=courseVersionUUID,
    institutionUUID=institutionUUID,
    registrationEnrollmentType=RegistrationEnrollmentType.cpf)))
        
 */ 
}