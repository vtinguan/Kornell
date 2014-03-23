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

  def update(enrollment: Enrollment): Enrollment = {
    sql"""
    | update Enrollment e
    | set e.enrolledOn = ${enrollment.getEnrolledOn},
    | e.class_uuid = ${enrollment.getCourseClassUUID},
    | e.person_uuid = ${enrollment.getPerson.getUUID},
    | e.progress = ${enrollment.getProgress},
    | e.notes = ${enrollment.getNotes},
    | e.state = ${enrollment.getState.toString}
    | where e.uuid = ${enrollment.getUUID}""".executeUpdate
    enrollment
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