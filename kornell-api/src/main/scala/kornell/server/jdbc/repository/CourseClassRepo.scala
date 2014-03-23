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
	  		cc.enrollWithCPF = ${courseClass.isEnrollWithCPF},
	  		cc.maxEnrollments = ${courseClass.getMaxEnrollments}
      where cc.uuid = ${courseClass.getUUID}""".executeUpdate
    courseClass
  }
  
  def actomsVisitedBy(p: Person): List[String] = sql"""
  	select actomKey from ActomEntered ae
  	join Enrollment e on ae.enrollmentUUID=e.uuid
  	where e.class_uuid = ${uuid}
  	and person_uuid = ${p.getUUID}
  	order by eventFiredAt
  	""".map[String]({ rs => rs.getString("actomKey") })
  	
  def getAdmins = newRoles(
    sql"""
	    | select *
      	| from Role r
        | where r.course_class_uuid = ${uuid}
        | order by r.username
	    """.map[Role](toRole))
  
  def updateAdmins(roles: Roles) = removeAdmins.addAdmins(roles)
  
  def addAdmins(roles: Roles) = {
    roles.getRoles.asScala.foreach(addRole _)
    roles
  }

  private def addRole(role: Role) = {
    if(RoleType.courseClassAdmin.equals(role.getRoleType()))
	    sql"""
	    	insert into Role (uuid, username, role, course_class_uuid)
	    	values (${UUID.random}, ${role.getUsername}, 
	    	${role.getRoleType.toString}, 
	    	${role.getCourseClassAdminRole.getCourseClassUUID} )
	    """.executeUpdate
  }
  
  def removeAdmins = {
    sql"""
    	delete from Role
    	where course_class_uuid = ${uuid}
    """.executeUpdate
    this
  }
  
}

object CourseClassRepo extends App {
  def apply(uuid:String) = new CourseClassRepo(uuid)
}