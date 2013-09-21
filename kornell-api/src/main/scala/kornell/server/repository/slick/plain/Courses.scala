package kornell.server.repository.slick.plain

import java.math.BigDecimal
import java.sql.Timestamp
import java.util.Date
import scala.math.BigDecimal.javaBigDecimal2bigDecimal
import scala.slick.jdbc.GetResult
import scala.slick.jdbc.SetParameter
import scala.slick.jdbc.StaticQuery.interpolation
import scala.slick.session.Database.threadLocalSession
import scala.slick.session.PositionedParameters
import javax.ws.rs.core.SecurityContext
import kornell.core.shared.to.CourseTO
import kornell.core.shared.to.CoursesTO
import kornell.core.shared.data.Enrollment
import kornell.core.shared.data.Person
import kornell.server.repository.Beans
import kornell.core.shared.data.Course
import kornell.server.repository.SlickRepository
import kornell.server.repository.TOs

object Courses extends SlickRepository with TOs {

  //TODO: Move this SetParameter to package object or Repository 
  implicit val SetDateTime: SetParameter[Date] = new SetParameter[Date] {
    def apply(d: Date, p: PositionedParameters): Unit =
      p setTimestamp (new Timestamp(d.getTime))
  }

  implicit val SetBigDecimal: SetParameter[BigDecimal] = new SetParameter[BigDecimal] {
    def apply(d: BigDecimal, p: PositionedParameters): Unit =
      p setBigDecimal (d)
  }

  //Conversions
  implicit val getCourseTO = GetResult(r => newCourseTO(r.nextString, r.nextString, r.nextString, r.nextString, r.nextString, r.nextString,
    r.nextString, r.nextDate, r.nextString, r.nextString, r.nextString))

  implicit def toCourses(l: List[CourseTO]): CoursesTO = newCoursesTO(l)

  //Queries
  def selectCourses(p: Person) = sql"""
		select c.uuid as courseUUID,c.code,c.title,c.description,c.assetsURL,c.infoJson,
			   e.uuid as enrollmentUUID, e.enrolledOn,e.person_uuid,e.progress,e.notes
		from Course c
		left join Enrollment e on c.uuid = e.course_uuid
		where e.person_uuid is null
		   or e.person_uuid = ${p.getUUID}
	"""

  def selectCourse(p: Person, uuid: String) = sql"""
		select c.uuid,c.code,c.title,c.description,c.assetsURL,c.infoJson,
			   e.uuid, e.enrolledOn,e.person_uuid,e.progress,e.notes
		from Course c
		left join Enrollment e on c.uuid = e.course_uuid
		where c.uuid = ${uuid} 
           and (e.person_uuid is null
		   or e.person_uuid = ${p.getUUID})
	"""

  def insert(c: Course) =
    sqlu"insert into Course values (${c.getUUID},${c.getCode},${c.getTitle},${c.getDescription},${c.getAssetsURL})"

  def create(title: String, code: String, description: String, assetsURL: String, infoJson: String) =
    db.withTransaction {
      val c = newCourse(randUUID, code, title, description.stripMargin, assetsURL, infoJson, "")
      insert(c).execute
      c
    }

  def createEnrollment(enrolledOn: Date, courseUUID: String, personUUID: String, progress: String, notes: String) =
    db.withTransaction {
      val e: Enrollment = (randUUID, enrolledOn, courseUUID, personUUID, new BigDecimal(progress), notes)
      sqlu"insert into Enrollment values (${e.getUUID},${e.getEnrolledOn},${e.getCourseUUID},${e.getPersonUUID},${e.getProgress})".execute
      e
    }
  
  def createEnrollmentNull(enrolledOn: Date, courseUUID: String, personUUID: String, progress: String, notes: String) =
    db.withTransaction {
      val e: Enrollment = (randUUID, enrolledOn, courseUUID, personUUID, null, notes)
      sqlu"insert into Enrollment values (${e.getUUID},${e.getEnrolledOn},${e.getCourseUUID},${e.getPersonUUID},${e.getProgress})".execute
      e
    }

  def allWithEnrollment(implicit sc: SecurityContext): CoursesTO = db.withSession {
    Persons.byUserPrincipal
      .map(selectCourses(_).as[CourseTO].list)
      .getOrElse(List[CourseTO]())
      .sortBy(to =>
        Option(to.getEnrollment.getProgress) match {
          case Some(progress) => progress
          case None => new BigDecimal("0.999")
        })
  }

  def byUUID(uuid: String)(implicit sc: SecurityContext): Option[CourseTO] = db.withSession {
    Persons.byUserPrincipal
      .flatMap { selectCourse(_, uuid).as[CourseTO].firstOption }
  }

}