package kornell.server.jdbc
import java.sql.Connection
import java.sql.ResultSet
import kornell.core.to.CourseTO
import kornell.core.entity.Enrollment
import kornell.server.repository.Entities._
import kornell.server.repository.TOs._
import kornell.core.entity.EnrollmentState
import kornell.core.entity.CourseClass
import kornell.core.to.CourseClassTO
import kornell.core.entity.Course
import kornell.server.repository.TOs
import kornell.core.entity.Person

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
  
  implicit def toCourseClassTO(r: ResultSet): CourseClassTO = 
    TOs.newCourseClassTO(   
    //course    
    r.getString("courseUUID"), 
    r.getString("code"), 
    r.getString("title"),
    r.getString("description"), 
    r.getString("infoJson"),
    //courseVersion
    r.getString("courseVersionUUID"), 
    r.getString("courseVersionName"), 
    r.getString("repositoryUUID"), 
    r.getDate("versionCreatedAt"),
    r.getString("distributionPrefix"),
    //courseClass
    r.getString("courseClassUUID"),
    r.getString("courseClassName"), 
    r.getString("institutionUUID"),
    r.getBigDecimal("requiredScore"),
    r.getBoolean("publicClass"),
    r.getBoolean("enrollWithCPF"),
    r.getInt("maxEnrollments"))
    

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

}
