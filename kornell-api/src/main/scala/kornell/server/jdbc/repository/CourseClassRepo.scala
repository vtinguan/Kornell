package kornell.server.jdbc.repository

import kornell.core.entity.CourseClass
import kornell.server.jdbc.SQL.SQLHelper
import kornell.server.jdbc.SQL.rsToString
import kornell.core.error.exception.EntityConflictException

class CourseClassRepo(uuid:String) {
  lazy val finder = sql"""
  select * from CourseClass where uuid=$uuid
  """
  
  def first = finder.first[CourseClass]
  
  def get = finder.get[CourseClass] 
  
  def version = CourseVersionRepo(get.getCourseVersionUUID)
  
  def update(courseClass: CourseClass): CourseClass = { 
    val courseClassExists = sql"""
    select count(*) from CourseClass where courseVersion_uuid = ${courseClass.getCourseVersionUUID} and name = ${courseClass.getName} and uuid <> ${courseClass.getUUID}
    """.first[String].get
    if (courseClassExists == "0") {
	    sql"""
	      update CourseClass cc set
			    cc.name = ${courseClass.getName},
			    cc.institution_uuid = ${courseClass.getInstitutionUUID},
		  		cc.requiredScore = ${courseClass.getRequiredScore},
		  		cc.publicClass = ${courseClass.isPublicClass},
		  		cc.overrideEnrollments = ${courseClass.isOverrideEnrollments},
		  		cc.invisible = ${courseClass.isInvisible},
		  		cc.maxEnrollments = ${courseClass.getMaxEnrollments},
		  		cc.registrationType = ${courseClass.getRegistrationType.toString},
		  		cc.institutionRegistrationPrefixUUID = ${courseClass.getInstitutionRegistrationPrefixUUID}
	      where cc.uuid = ${courseClass.getUUID}""".executeUpdate
	    ChatThreadsRepo.updateCourseClassThreadsNames(courseClass.getUUID, courseClass.getName)
	    courseClass
    } else {
      throw new EntityConflictException("courseClassAlreadyExists")
    }   
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
