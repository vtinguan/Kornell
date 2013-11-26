package kornell.server.repository.jdbc

import kornell.server.repository.TOs
import kornell.core.to.RegistrationsTO
import kornell.server.repository.jdbc.SQLInterpolation._
import kornell.server.repository.Entities
import java.sql.ResultSet
import kornell.core.entity.Registration
import kornell.core.entity.Institution
import scala.collection.JavaConverters._
import kornell.core.entity.Person
import kornell.server.repository.Entities._
import kornell.server.repository.TOs._
import kornell.core.entity.Enrollment
import scala.collection.mutable.ListBuffer
import kornell.core.to.CourseTO
import kornell.core.entity.EnrollmentState
import scala.None
import java.util.Date
import kornell.server.util.EmailSender
import kornell.core.entity.Course
import kornell.server.repository.jdbc.CourseClasses
import kornell.core.entity.CourseClass
import kornell.core.util.UUID
import kornell.server.repository.service.RegistrationEnrollmentService

class Enrollments() {

  def byCourseClass(courseClassUUID: String) = newEnrollments(
    sql"""
	    | select e.uuid, e.enrolledOn, e.class_uuid, e.person_uuid, e.progress, e.notes, e.state
      	| from Enrollment e join Person p on e.person_uuid = p.uuid
        | where e.class_uuid = ${courseClassUUID}
        | order by e.state desc, p.fullName, p.email
	    """.map[Enrollment](toEnrollment))

  def byCourseClassAndPerson(courseClassUUID: String, personUUID: String): Option[Enrollment] =
    sql"""
	    | select e.uuid, e.enrolledOn, e.class_uuid, e.person_uuid, e.progress, e.notes, e.state
      	| from Enrollment e join Person p on e.person_uuid = p.uuid
        | where e.class_uuid = ${courseClassUUID} and
        | e.person_uuid = ${personUUID}
        | order by e.state desc, p.fullName, p.email
	    """.first[Enrollment]

  def createEnrollment(courseClassUUID: String, person_uuid: String, state: EnrollmentState) = {
    val uuid = randomUUID
    sql""" 
    	insert into Enrollment(uuid,class_uuid,person_uuid,enrolledOn,state)
    	values($randomUUID,$courseClassUUID,$person_uuid,now(),${state.toString()})
    """.executeUpdate
  }

}

object Enrollments {
  def apply(): Enrollments =
    new Enrollments()
}

