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

package object jdbc {
  type UUID = String
  type ConnectionFactory = () => Connection
//(uuid:String,name:String,courseVersionUUID:String,institutionUUID:String)
  
  implicit def toCourseClass(r: ResultSet): CourseClass = 
    newCourseClass(r.getString("uuid"), r.getString("name"), 
        r.getString("courseVersion_uuid"), r.getString("institution_uuid")) 
    
  
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
    //courseClass
    r.getString("courseClassUUID"),
    r.getString("courseClassName"), 
    r.getString("institutionUUID"),
    //enrollment
    r.getString("enrollmentUUID"), 
    r.getDate("enrolledOn"), 
    r.getString("personUUID"), 
    r.getString("progress"), 
    r.getString("notes"), 
    r.getString("enrollmentState"))
    

  implicit def toEnrollment(rs: ResultSet): Enrollment =
    newEnrollment(
      rs.getString("uuid"),
      rs.getDate("enrolledOn"),
      rs.getString("class_uuid"),
      rs.getString("person_uuid"),
      rs.getBigDecimal("progress"),
      rs.getString("notes"),
      EnrollmentState.valueOf(rs.getString("state")))
      
    

}