package kornell.server.jdbc.repository
import kornell.server.jdbc.SQL._
import kornell.core.entity.Person
import java.sql.ResultSet
import kornell.server.repository.Entities
import kornell.server.repository.Entities._
import java.util.Date
import kornell.core.entity.RoleCategory
import scala.collection.JavaConverters._
import kornell.core.entity.RoleType

class PersonRepo(val uuid:String) {
  
	def setPassword(username:String, password:String):PersonRepo = {
	  AuthRepo.setPlainPassword(uuid, username, password)
	  PersonRepo.this
	}
	
	def registerOn(institution_uuid:String):RegistrationRepo = {
	  (RegistrationRepo(PersonRepo.this, institution_uuid)).register
	}
	    
	def get = sql"""select * from Person where uuid=$uuid""".first[Person]

	def update(person: Person) = {
	    sql"""
	    	update Person set fullName = ${person.getFullName},
	    	email = ${person.getEmail}, company = ${person.getCompany}, title = ${person.getTitle},
	    	sex = ${person.getSex}, birthDate = ${person.getBirthDate}, confirmation = ${person.getConfirmation},
	    	telephone = ${person.getTelephone}, country = ${person.getCountry}, state = ${person.getState}, city = ${person.getCity}, 
	    	addressLine1 = ${person.getAddressLine1}, addressLine2 = ${person.getAddressLine2}, postalCode = ${person.getPostalCode},
	    	cpf = ${person.getCPF}
	    	where uuid = $uuid
	    """.executeUpdate
      PersonRepo.this
    }

  def hasPowerOver(targetPersonUUID: String) = {
      val actorRoles = AuthRepo.rolesOf(AuthRepo.getUsernameByPersonUUID(uuid))
	    val actorRolesSet = (Set.empty ++ actorRoles).asJava
      
	    val targetRoles = AuthRepo.rolesOf(AuthRepo.getUsernameByPersonUUID(targetPersonUUID))
      val targetRolesSet = (Set.empty ++ targetRoles).asJava

      //people have power over themselves
	    (uuid == targetPersonUUID) ||
	    {
	    	//platformAdmin has power over everyone, except other platformAdmins
	      !RoleCategory.isPlatformAdmin(targetRolesSet) && 
	      RoleCategory.isPlatformAdmin(actorRolesSet)
	    } || {
		    //institutionAdmin doesn't have power over platformAdmins, other institutionAdmins or non registered users 
	      val registrations = RegistrationsRepo.getAll(targetPersonUUID)
		    !RoleCategory.isPlatformAdmin(targetRolesSet) && 
		    		!RoleCategory.hasRole(targetRolesSet, RoleType.institutionAdmin) && {
	        registrations.getRegistrations.asScala exists {
	          r => RoleCategory.isInstitutionAdmin(actorRolesSet, r.getInstitutionUUID) 
	        }
	      }
	    } || {
			  //courseClassAdmin doesn't have power over platformAdmins, institutionAdmins, other courseClassAdmins or non enrolled users
	      val enrollments = EnrollmentsRepo.byPerson(targetPersonUUID)
		    !RoleCategory.isPlatformAdmin(targetRolesSet) && 
		    		!RoleCategory.hasRole(targetRolesSet, RoleType.institutionAdmin) &&
		    		!RoleCategory.hasRole(targetRolesSet, RoleType.courseClassAdmin) && {
	        enrollments exists {
	          e => RoleCategory.isCourseClassAdmin(actorRolesSet, e.getCourseClassUUID)
	        }
		    } 
	    }
	}
	
}

object PersonRepo{
  def apply(uuid:String) = new PersonRepo(uuid)
}