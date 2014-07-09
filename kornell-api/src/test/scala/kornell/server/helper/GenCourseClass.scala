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


trait GenCourseClass
	extends GenInstitution
	with Generator {
  
  val classCode = randStr(5)
  
  val course = CoursesResource().create(code = classCode)
  val courseUUID = course.getUUID
  
  val courseVersion = {
    val repositoryUUID = RepositoriesRepo().createS3Repository().getUUID()
    CourseVersionsResource(courseUUID).create(repositoryUUID = repositoryUUID)  
  }
  val courseVersionUUID = courseVersion.getUUID
  
	def newCourseClass:CourseClass =     
    CourseClassesResource(courseVersionUUID).createCourseClass(institutionUUID)
   
}