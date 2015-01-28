package kornell.server.jdbc.repository

import java.sql.ResultSet
import scala.collection.JavaConverters._
import kornell.core.entity.Course
import kornell.core.entity.Course
import kornell.server.jdbc.SQL._
import kornell.server.jdbc.SQL._
import kornell.server.repository.Entities._
import kornell.server.repository.TOs._
import kornell.server.repository.Entities
import kornell.server.repository.TOs
import kornell.core.entity.Role
import kornell.core.util.UUID
import kornell.core.entity.Roles
import kornell.core.entity.RoleType
import kornell.core.entity.RoleCategory

object RolesRepo {
  	
  def getCourseClassAdmins(courseClassUUID: String, bindMode: String) =
	  TOs.newRolesTO(sql"""
		    | select *
	      	| from Role r
	        | where r.course_class_uuid = ${courseClassUUID}
	  			| and r.role = ${RoleType.courseClassAdmin.toString}
		    """.map[Role](toRole).map(bindRole(_, bindMode)))	
  	
  def getInstitutionAdmins(institutionUUID: String, bindMode: String) =
	  TOs.newRolesTO(sql"""
		    | select *
	      	| from Role r
	        | where r.institution_uuid = ${institutionUUID}
	  			| and r.role = ${RoleType.institutionAdmin.toString}
		    """.map[Role](toRole).map(bindRole(_, bindMode)))	
  	
  def getPlatformAdmins(bindMode: String) =
	  TOs.newRolesTO(sql"""
		    | select *
	      	| from Role r
	  			| and r.role = ${RoleType.platformAdmin.toString}
		    """.map[Role](toRole).map(bindRole(_, bindMode)))	
  	
  def getCourseClassThreadSupportParticipants(courseClassUUID: String, institutionUUID: String, bindMode: String) =
	  TOs.newRolesTO(sql"""
		    | select *
	      	| from Role r
	        | where (r.course_class_uuid = ${courseClassUUID}
	  			| 	and r.role = ${RoleType.courseClassAdmin.toString})
	  			| or  (r.institution_uuid = ${institutionUUID}
	  			| 	and r.role = ${RoleType.institutionAdmin.toString})
	  			| or r.role = ${RoleType.platformAdmin.toString}
		    """.map[Role](toRole).map(bindRole(_, bindMode)))	
  	
  def getInstitutionSupportResponsible(institutionUUID: String) =
	  bindRole(sql"""
		    | select *
	      	| from Role r
	        | where r.institution_uuid = ${institutionUUID}
		    """.first[Role](toRole).get, RoleCategory.BIND_WITH_PERSON).getPerson
		    
  def getTutorsForCourseClass(courseClassUUID: String, bindMode: String)=
      TOs.newRolesTO(sql"""
            | select *
            | from Role r
            | where courseClassUUID = ${courseClassUUID}
                | and r.role = ${RoleType.tutor.toString}
            """.map[Role](toRole).map(bindRole(_, bindMode)))   

  private def bindRole(role: Role, bindMode: String) =
    TOs.newRoleTO(role, {
      if(role != null && RoleCategory.BIND_WITH_PERSON.equals(bindMode))
        PeopleRepo.getByUUID(role.getPersonUUID).get
      else
        null
    })
  
  
  def updateCourseClassAdmins(courseClassUUID: String, roles: Roles) = removeCourseClassAdmins(courseClassUUID).addRoles(roles)
  
  def updateInstitutionAdmins(institutionUUID: String, roles: Roles) = removeInstitutionAdmins(institutionUUID).addRoles(roles)
  
  def updateTutors(courseClassUUID: String, roles: Roles) = removeTutors(courseClassUUID).addRoles(roles)
  
  def addRoles(roles: Roles) = {
    roles.getRoles.asScala.foreach(addRole _)
    roles
  }

  private def addRole(role: Role) = {
    if(RoleType.courseClassAdmin.equals(role.getRoleType)) {
	    sql"""
	    	insert into Role (uuid, person_uuid, role, course_class_uuid)
	    	values (${UUID.random}, 
    		${role.getPersonUUID}, 
	    	${role.getRoleType.toString}, 
	    	${role.getCourseClassAdminRole.getCourseClassUUID})
	    """.executeUpdate
    }
    if (RoleType.institutionAdmin.equals(role.getRoleType)) {
      sql"""
            insert into Role (uuid, person_uuid, role, institution_uuid)
            values (${UUID.random}, 
            ${role.getPersonUUID}, 
            ${role.getRoleType.toString}, 
            ${role.getInstitutionAdminRole.getInstitutionUUID})
        """.executeUpdate
    }
  }
  
  def removeCourseClassAdmins(courseClassUUID: String) = {
    sql"""
    	delete from Role
    	where course_class_uuid = ${courseClassUUID}
        and role = ${RoleType.courseClassAdmin}
    """.executeUpdate
    this
  }
  
  def removeInstitutionAdmins(institutionUUID: String) = {
    sql"""
        delete from Role
        where institution_uuid = ${institutionUUID}
    """.executeUpdate
    this
  }
  
  def removeTutors(courseClassUUID: String) = {
    sql"""
        delete from Role
        where course_class_uuid = ${courseClassUUID}
        and role = ${RoleType.tutor}
    """.executeUpdate
    this
  }
  
}