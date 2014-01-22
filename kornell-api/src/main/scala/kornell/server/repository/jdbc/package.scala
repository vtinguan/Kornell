package kornell.server.repository

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

package object jdbc {
  //type UUID = String
  type ConnectionFactory = () => Connection
  
  implicit def toCourseClass(rs: ResultSet): CourseClass = newCourseClass(
    rs.getString("uuid"), 
    rs.getString("name"), 
    rs.getString("courseVersion_uuid"),
    rs.getString("institution_uuid")) 

  implicit def toCourse(rs: ResultSet): Course = newCourse(
    rs.getString("uuid"),
    rs.getString("code"),
    rs.getString("title"),
    rs.getString("description"),
    rs.getString("infoJson"))    
  
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
    //courseClass
    rs.getString("courseClassUUID"),
    rs.getString("courseClassName"), 
    rs.getString("institutionUUID"),
    //enrollment
    rs.getString("enrollmentUUID"), 
    rs.getDate("enrolledOn"), 
    rs.getString("personUUID"), 
    rs.getString("progress"), 
    rs.getString("notes"), 
    rs.getString("enrollmentState"))
    
  def prop(name: String) = System.getProperty(name)

  val DEFAULT_URL = "jdbc:mysql:///ebdb"
  val DEFAULT_USERNAME= "kornell"
  val DEFAULT_PASSWORD= "42kornell73"

  implicit def toEnrollment(rs: ResultSet): Enrollment =
    newEnrollment(
      rs.getString("uuid"),
      rs.getDate("enrolledOn"),
      rs.getString("class_uuid"),
      rs.getString("person_uuid"),
      rs.getInt("progress"),
      rs.getString("notes"),
      EnrollmentState.valueOf(rs.getString("state")))
      
    

}