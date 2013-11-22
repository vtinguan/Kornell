package kornell.server.repository

import java.sql.Connection
import java.sql.ResultSet
import kornell.core.to.CourseTO
package object jdbc {
  type UUID = String
  type ConnectionFactory = () => Connection

  /*
  implicit def newCourseTO(r: ResultSet): CourseTO = 
    TOs.newCourseTO( 
    r.getString("courseUUID"), r.getString("code"), r.getString("title"),
    r.getString("description"), r.getString("infoJson"),
    r.getString("enrollmentUUID"), r.getDate("enrolledOn"),
    r.getString("person_uuid"), r.getString("progress"), r.getString("repository_uuid"), r.getString("notes"))
    * 
    */
}