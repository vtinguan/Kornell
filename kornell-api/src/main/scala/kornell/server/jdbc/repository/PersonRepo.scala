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
import java.text.SimpleDateFormat
import java.text.DateFormat

class PersonRepo(val uuid: String) {

  def setPassword(username: String, password: String): PersonRepo = {
    AuthRepo.setPlainPassword(uuid, username, password)
    PersonRepo.this
  }

  def registerOn(institution_uuid: String): RegistrationRepo = {
    (RegistrationRepo(PersonRepo.this, institution_uuid)).register
  }

  def get = sql"""select * from Person where uuid=$uuid""".first[Person]

  def update(person: Person) = {
    //TODO timezone issue 
    //person.getBirthDate.setHours(5)
    val sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
    val msg = s"*#* Changing birth date of person [${person.getUUID()}] to [${sdf.format(person.getBirthDate())}] time [${person.getBirthDate().toJUD().getTime()}]"
    println(msg)
    logger.warning(msg)
    
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
    val actorRoles = AuthRepo.rolesOf(AuthRepo.getUsernameByPersonUUID(uuid).get)
    val actorRolesSet = (Set.empty ++ actorRoles).asJava

    val targetUsername = AuthRepo.getUsernameByPersonUUID(targetPersonUUID)

    //if there's no username yet, any admin can have power
    (!targetUsername.isDefined) ||
      {
        val targetRoles = AuthRepo.rolesOf(targetUsername.get)
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

}

object PersonRepo {
  def apply(uuid: String) = new PersonRepo(uuid)
}