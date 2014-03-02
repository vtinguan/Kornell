package kornell.server.jdbc.repository

import kornell.core.entity.Enrollment
import java.sql.ResultSet
import kornell.server.repository.Entities
import kornell.server.jdbc.SQL._
import kornell.server.repository.Entities._
import kornell.core.entity.Enrollment
import kornell.core.entity.EnrollmentState

class EnrollmentRepo(enrollmentUUID: String) {
  lazy val finder = sql"""
	    | select e.uuid, e.enrolledOn, e.class_uuid, e.person_uuid, e.progress, e.notes, e.state
      	| from Enrollment e where uuid = ${enrollmentUUID} 
	    """

  def get:Enrollment = finder.get[Enrollment]
  
  def first: Option[Enrollment] =
    finder.first[Enrollment]

  def enroll(courseUUID: String, personUUID: String) = {
    val uuid = randomUUID
    sql""" 
    	insert into Enrollment(uuid,course_uuid,person_uuid,enrolledOn)
    	values($randomUUID,${courseUUID},${personUUID} ,now())
    """.executeUpdate
    None
  }

  def findGrades: List[String] = sql"""
    	select * from ActomEntries
    	where enrollment_uuid = ${enrollmentUUID}
    	and entryKey = 'cmi.core.score.raw'
    """.map { rs => rs.getString("entryValue") }
}

object EnrollmentRepo {
  def apply(uuid: String) = new EnrollmentRepo(uuid)
}