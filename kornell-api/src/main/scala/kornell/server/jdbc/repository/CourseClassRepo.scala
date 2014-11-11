package kornell.server.jdbc.repository

import kornell.server.jdbc.SQL._
import java.sql.ResultSet
import kornell.core.entity.CourseClass
import kornell.server.repository.Entities
import kornell.core.entity.Person
import kornell.core.to.CourseClassTO
import kornell.core.to.CourseClassesTO
import kornell.core.entity.Roles
import kornell.core.entity.Role
import kornell.core.entity.RoleType
import scala.collection.JavaConverters._
import kornell.core.util.UUID
import kornell.server.repository.Entities.newRoles
import kornell.core.to.RoleTO
import kornell.core.to.TOFactory
import kornell.server.repository.TOs
import kornell.core.entity.RoleCategory

class CourseClassRepo(uuid:String) {
  lazy val finder = sql"""
  select * from CourseClass where uuid=$uuid
  """
  
  def first = finder.first[CourseClass]
  
  def get = finder.get[CourseClass] 
  
  def version = CourseVersionRepo(get.getCourseVersionUUID())
  
  def update(courseClass: CourseClass): CourseClass = {    
    sql"""
      update CourseClass cc set
		    cc.name = ${courseClass.getName},
		    cc.institution_uuid = ${courseClass.getInstitutionUUID},
	  		cc.requiredScore = ${courseClass.getRequiredScore},
	  		cc.publicClass = ${courseClass.isPublicClass},
	  		cc.overrideEnrollments = ${courseClass.isOverrideEnrollments},
	  		cc.invisible = ${courseClass.isInvisible},
	  		cc.maxEnrollments = ${courseClass.getMaxEnrollments}
	  		cc.registrationEnrollmentType = ${courseClass.getRegistrationEnrollmentType}
	  		cc.institutionRegistrationPrefix = ${courseClass.getInstitutionRegistrationPrefix}
      where cc.uuid = ${courseClass.getUUID}""".executeUpdate
    ChatThreadsRepo.updateCourseClassSupportThreadsNames(courseClass.getUUID, courseClass.getName)
    courseClass
  }
  
  def delete(courseClassUUID: String) = {    
    sql"""
      delete from CourseClass 
      where uuid = ${courseClassUUID}""".executeUpdate
  }
  
  def actomsVisitedBy(personUUID: String): List[String] = sql"""
  	select actomKey from ActomEntered ae
  	join Enrollment e on ae.enrollmentUUID=e.uuid
  	where e.class_uuid = ${uuid}
  	and person_uuid = ${personUUID}
  	order by eventFiredAt
  	""".map[String]({ rs => rs.getString("actomKey") })
  
}

object CourseClassRepo extends App {
  def apply(uuid:String) = new CourseClassRepo(uuid)
}
