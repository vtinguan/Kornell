package kornell.server.repository.jdbc

import kornell.server.repository.jdbc.SQLInterpolation.SQLHelper
import java.sql.ResultSet
import kornell.core.entity.CourseClass
import kornell.server.repository.Entities
import kornell.core.entity.Person
import kornell.core.to.CourseClassTO
import kornell.core.to.CourseClassesTO

class CourseClassRepository(uuid:String) {
  
  def get = sql"""
  select * from CourseClass where uuid=$uuid
  """.get[CourseClass]
  
  def version = CourseVersionRepository(get.getCourseVersionUUID())
  
  def actomsVisitedBy(p: Person): List[String] = sql"""
  	select actom_key from ActomEntered 
  	where courseClass_uuid = ${uuid}
  	and person_uuid = ${p.getUUID}
  	order by eventFiredAt
  	""".map[String]({ rs => rs.getString("actom_key") })
  
  def byPerson(personUUID: String): CourseClassTO = {
    val courseClasses = sql"""
			select     
				c.uuid as courseUUID, 
			    c.code,
			    c.title, 
			    c.description,
			    c.infoJson,
			    cv.uuid as courseVersionUUID,
			    cv.name as courseVersionName,
			    cv.repository_uuid as repositoryUUID, 
			    cv.versionCreatedAt,
			    cc.uuid as courseClassUUID,
			    cc.name as courseClassName,
			    cc.institution_uuid as institutionUUID,
			    e.uuid as enrollmentUUID, 
			    e.enrolledOn, 
			    e.person_uuid as personUUID, 
			    e.progress,
			    e.notes,
			    e.state as enrollmentState
			from Course c
			left join CourseVersion cv on cv.course_uuid = c.uuid
			left join CourseClass cc on cc.courseVersion_uuid = cv.uuid
			left join Enrollment e on cc.uuid = e.class_uuid
			where e.person_uuid = ${personUUID};
		""".map[CourseClassTO](toCourseClassTO)
		if(courseClasses.size > 0)
		  courseClasses.head
		else
		  null
  	}
}

object CourseClassRepository extends App{
  override def main(args: Array[String]) {
	apply("B6A60AB5-3889-47B4-93DA-60E515309DAF").byPerson("c1ee8adf-ba23-4f7e-80be-9ac3a6dde7b7")
  }
  def apply(uuid:String) = new CourseClassRepository(uuid)
}