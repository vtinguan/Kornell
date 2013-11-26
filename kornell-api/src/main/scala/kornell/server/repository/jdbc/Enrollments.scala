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
import kornell.core.entity.CourseClass

class Enrollments() {

  def byCourse(courseUUID: String) = newEnrollments(
    sql"""
	    | select e.uuid, e.enrolledOn, e.course_uuid, e.person_uuid, e.progress, e.notes, e.state
      	| from Enrollment e join Person p on e.person_uuid = p.uuid
        | where e.course_uuid = ${courseUUID}
        | order by e.state desc, p.fullName, p.email
	    """.map[Enrollment](toEnrollment))

  def byCourseAndPerson(courseUUID: String, personUUID: String): Option[Enrollment] =
    sql"""
	    | select e.uuid, e.enrolledOn, e.course_uuid, e.person_uuid, e.progress, e.notes, e.state
      	| from Enrollment e join Person p on e.person_uuid = p.uuid
        | where e.course_uuid = ${courseUUID} and
        | and e.person_uuid = ${personUUID}
        | order by e.state desc, p.fullName, p.email
	    """.first[Enrollment]

  def createEnrollmentsBatch(enrollments: kornell.core.entity.Enrollments) =
    enrollments.getEnrollments().asScala.foreach(createEnrollmentBatch)

  def createEnrollmentBatch(enrollment: Enrollment) = {
    val clazz = Classes(enrollment.getCourseClassUUID()).get
    // create person if it doesn't exist
    Auth.getPerson(enrollment.getPerson.getEmail) match {
      case Some(one) => enrollExistingPerson(clazz, one)
      case None => enrollNewPerson(clazz, enrollment.getPerson.getEmail, enrollment.getPerson.getFullName)
    }
  }

  private def enrollNewPerson(clazz: CourseClass, email: String, fullName: String): Unit = {
    //TODO: URG: fix date nulls
    val personRepo = People().createPerson(email, fullName, "", "", "", "1800-01-01", "")
    val person = personRepo.get.get
    personRepo.registerOn(clazz.getInstitutionUUID)
    createEnrollment(clazz.getUUID, person.getUUID(), EnrollmentState.preEnrolled)
    //envia email pro cara
    //??? confirmation link
    //sem fullName???
    EmailSender.sendEmail(person, Institutions.byUUID(clazz.getInstitutionUUID).get, ???)
  }

  private def enrollExistingPerson(clazz: CourseClass, person: Person) =
    byCourseAndPerson(clazz.getUUID, person.getUUID) match {
      case Some(enrollment) => updateExistingEnrollment(clazz.getInstitutionUUID, person, enrollment)
      case None => createNewEnrollment(clazz, person)
    }

  private def createNewEnrollment(clazz: CourseClass, person: Person): Unit = {
    createEnrollment(clazz.getUUID, person.getUUID, EnrollmentState.preEnrolled)
    //envia email pro cara
    //??? confirmation link
    //sem fullName???
    EmailSender.sendEmail(person, Institutions.byUUID(clazz.getInstitutionUUID).get, ???)

  }

  private def updateExistingEnrollment(institutionUUID: String, person: Person, enrollment: Enrollment): Unit = {
    //fromPersonUUID
    Events.logEnrollmentStateChanged(randomUUID, new Date(), ???, enrollment.getUUID(), enrollment.getState(), EnrollmentState.enrolled)
    //envia email pro cara
    //??? confirmation link
    //sem fullName???
    EmailSender.sendEmail(person, Institutions.byUUID(institutionUUID).get, ???)
  }

  def createEnrollment(clazzUUID: String, person_uuid: String, state: EnrollmentState) = {
    val uuid = randomUUID
    sql""" 
    	insert into Enrollment(uuid,courseClass_uuid,person_uuid,enrolledOn,state)
    	values($randomUUID,$clazzUUID,$person_uuid,now(),${state.toString()})
    """.executeUpdate
  }

}

object Enrollments {
  def apply(): Enrollments =
    new Enrollments()
}

