package kornell.repository.slick.plain

import java.math.BigDecimal
import java.util.Date
import scala.slick.driver.BasicProfile
import scala.slick.jdbc.StaticQuery.interpolation
import scala.slick.lifted.BaseTypeMapper
import scala.slick.lifted.TypeMapperDelegate
import scala.slick.session.Database.threadLocalSession
import scala.slick.session.PositionedParameters
import scala.slick.session.PositionedResult
import kornell.repository.Beans
import kornell.util.DataURI
import java.sql.Timestamp
import scala.slick.lifted.MappedTypeMapper
import scala.slick.jdbc.SetParameter
import kornell.core.shared.data.Enrollment
import kornell.core.shared.data.CourseTO
import scala.slick.jdbc.GetResult
import kornell.core.shared.data.CoursesTO


object Courses extends Repository with Beans {

  implicit val SetDateTime: SetParameter[Date] = new SetParameter[Date] {
    def apply(d: Date, p: PositionedParameters): Unit =
      p setTimestamp (new Timestamp(d.getTime))
  }

  implicit val SetBigDecimal: SetParameter[BigDecimal] = new SetParameter[BigDecimal] {
    def apply(d: BigDecimal, p: PositionedParameters): Unit =
      p setBigDecimal (d)
  }

  implicit val getCourseTO = GetResult(r => newCourseTO(r.nextString,r.nextString,r.nextString,r.nextString,r.nextString,
		  												r.nextString,r.nextDate,r.nextString,r.nextString))

  def create(title: String, code: String, description: String, resourceName: String) =
    db.withTransaction {
      val c = newCourse(randUUID, code, title, description.stripMargin, DataURI.fromResource(resourceName).get)
      sqlu"insert into Course values (${c.getUUID},${c.getCode},${c.getTitle},${c.getDescription},${c.getThumbDataURI})".execute
      c
    }

  def createEnrollment(enrolledOn: Date, courseUUID: String, personUUID: String, progress: String) =
    db.withTransaction {
      val e: Enrollment = (randUUID, enrolledOn, courseUUID, personUUID, new BigDecimal(progress))
      sqlu"insert into Enrollment values (${e.getUUID},${e.getEnrolledOn},${e.getCourseUUID},${e.getPersonUUID},${e.getProgress})".execute
      e
  }
  
  implicit def toCourses(l:List[CourseTO]):CoursesTO = newCoursesTO(l)
  
  def allWithEnrollment(username:String):CoursesTO = db.withSession {
    val person = Persons.byUsername(username)
    (person match {
      case Some(p) => sql"""
		select c.uuid,c.code,c.title,c.description,c.thumbDataURI,
			   e.uuid, e.enrolledOn,e.person_uuid,e.progress
		from Course c
		left join Enrollment e on c.uuid = e.course_uuid
		where e.person_uuid is null
		   or e.person_uuid = ${p.getUUID()}
	""".as[CourseTO].list	
      case None => List.empty
    }).sortBy { to =>
        val progress = to.getEnrollment.getProgress
        if(progress == null)
          new BigDecimal("0.999") //not tryed just before completed
        else progress
    }
  }

}