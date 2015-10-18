package kornell.server.helper

import kornell.server.repository.Entities
import java.util.Date
import kornell.core.entity.CourseClassState
import kornell.server.api.CoursesResource
import kornell.server.api.CourseVersionsResource
import kornell.server.api.CourseClassesResource
import kornell.core.entity.CourseClass
import kornell.server.api.RepositoryResource
import kornell.server.jdbc.repository.RepositoriesRepo
import kornell.core.entity.RegistrationType
import kornell.server.jdbc.repository.CourseVersionsRepo
import kornell.server.jdbc.repository.CoursesRepo


trait GenCourseClass
	extends GenInstitution
	with Generator {
  
  val classCode = randStr(5)
  
  val course = CoursesRepo.create(Entities.newCourse(randUUID, classCode, null, null, null, null, false))
  val courseUUID = course.getUUID
  
  val courseVersion = {
    val repositoryUUID = RepositoriesRepo().createS3Repository("", "", "", institutionUUID = institutionUUID, region = "us-east-1").getUUID
    CourseVersionsRepo.create(Entities.newCourseVersion(repositoryUUID=repositoryUUID, courseUUID = courseUUID), institutionUUID)
  }
  val courseVersionUUID = courseVersion.getUUID
   
  def newPublicCourseClass = CourseClassesResource().create((Entities.newCourseClass(
    courseVersionUUID=courseVersionUUID,
    institutionUUID=institutionUUID,
    registrationType=RegistrationType.email,
    publicClass=true)))
        
  def newCourseClassCpf:CourseClass = CourseClassesResource().create((Entities.newCourseClass(
    name=randStr(5),
    courseVersionUUID=courseVersionUUID,
    institutionUUID=institutionUUID,
    registrationType=RegistrationType.cpf)))
        
  def newCourseClassEmail:CourseClass = CourseClassesResource().create((Entities.newCourseClass(
    name=randStr(5),
    courseVersionUUID=courseVersionUUID,
    institutionUUID=institutionUUID,
    registrationType=RegistrationType.email)))
}