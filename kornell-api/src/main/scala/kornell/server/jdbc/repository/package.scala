package kornell.server.jdbc
import java.sql.Connection
import java.sql.ResultSet
import kornell.core.entity.Enrollment
import kornell.server.repository.Entities._
import kornell.server.repository.TOs._
import kornell.core.entity.EnrollmentState
import kornell.core.entity.CourseClass
import kornell.core.to.CourseClassTO
import kornell.core.entity.Course
import kornell.server.repository.TOs
import kornell.core.entity.Person
import kornell.core.entity.CourseVersion
import kornell.core.entity.RoleType
import kornell.server.repository.Entities
import java.util.logging.Logger
import kornell.core.util.UUID

/**
 * Classes in this package are Data Access Objects for JDBC Databases
 *
 * This is the naming convention for methods in this package:
 *
 * first() => Return Option[T], when the result may not exist
 * get() => Returns T, presuming the result exists
 * find() => Return Collection[T], as the result of a query
 */
package object repository {
  val logger = Logger.getLogger("kornell.server.jdbc")
  
  implicit def toCourseClass(r: ResultSet): CourseClass = 
    newCourseClass(r.getString("uuid"), r.getString("name"), 
        r.getString("courseVersion_uuid"), r.getString("institution_uuid"),
        r.getBigDecimal("requiredScore"), r.getBoolean("publicClass"), 
        r.getBoolean("enrollWithCPF"), r.getInt("maxEnrollments")) 

  implicit def toCourse(rs: ResultSet): Course = newCourse(
    rs.getString("uuid"),
    rs.getString("code"),
    rs.getString("title"),
    rs.getString("description"),
    rs.getString("infoJson"))    

  implicit def toCourseVersion(rs: ResultSet): CourseVersion = newCourseVersion(
    rs.getString("uuid"), 
    rs.getString("name"), 
    rs.getString("course_uuid"), 
    rs.getString("repository_uuid"), 
    rs.getDate("versionCreatedAt"),
    rs.getString("distributionPrefix"),
    rs.getString("contentSpec"))    
  
  implicit def toCourseClassTO(rs: ResultSet): CourseClassTO = 
    TOs.newCourseClassTO(   
    //course    
    rs.getString("courseUUID"), 
    rs.getString("code"), 
    rs.getString("title"),
    rs.getString("description"), 
    rs.getString("infoJson"),
    //courseVersion
    rs.getString("courseVersionUUID"), 
    rs.getString("courseVersionName"), 
    rs.getString("repositoryUUID"), 
    rs.getDate("versionCreatedAt"),
    rs.getString("distributionPrefix"),
    rs.getString("contentSpec"),
    //courseClass
    rs.getString("courseClassUUID"),
    rs.getString("courseClassName"), 
    rs.getString("institutionUUID"),
    rs.getBigDecimal("requiredScore"),
    rs.getBoolean("publicClass"),
    rs.getBoolean("enrollWithCPF"),
    rs.getInt("maxEnrollments"))
    

  implicit def toEnrollment(rs: ResultSet): Enrollment =
    newEnrollment(
      rs.getString("uuid"),
      rs.getDate("enrolledOn"),
      rs.getString("class_uuid"),
      rs.getString("person_uuid"),
      rs.getInt("progress"),
      rs.getString("notes"),
      EnrollmentState.valueOf(rs.getString("state")))
	
	implicit def toPerson(rs:ResultSet):Person = newPerson(
	    rs.getString("uuid"),
	    rs.getString("fullName"), 
	    rs.getString("lastPlaceVisited"),
	    rs.getString("email"),
	    rs.getString("company"),
	    rs.getString("title"),
	    rs.getString("sex"),
	    rs.getDate("birthDate"),
	    rs.getString("confirmation"),
	    rs.getString("telephone"),
	    rs.getString("country"),
	    rs.getString("state"),
	    rs.getString("city"),
	    rs.getString("addressLine1"),
	    rs.getString("addressLine2"),
	    rs.getString("postalCode"),
	    rs.getString("cpf"))

  implicit def toRole(rs: java.sql.ResultSet): kornell.core.entity.Role = {
    val roleType = RoleType.valueOf(rs.getString("role"))
    val role = roleType match {
      case RoleType.user => Entities.newUserRole
      case RoleType.platformAdmin => Entities.newPlatformAdminRole(rs.getString("username"))
      case RoleType.institutionAdmin => Entities.newInstitutionAdminRole(rs.getString("username"), rs.getString("institution_uuid"))
      case RoleType.courseClassAdmin => Entities.newCourseClassAdminRole(rs.getString("username"), rs.getString("course_class_uuid"))
    }
    role
  }
  


}
