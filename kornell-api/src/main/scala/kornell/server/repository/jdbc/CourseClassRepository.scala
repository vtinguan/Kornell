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
  	select actomKey from ActomEntered 
  	where courseClass_uuid = ${uuid}
  	and person_uuid = ${p.getUUID}
  	order by eventFiredAt
  	""".map[String]({ rs => rs.getString("actomKey") })
}

object CourseClassRepository extends App{
  def apply(uuid:String) = new CourseClassRepository(uuid)
}