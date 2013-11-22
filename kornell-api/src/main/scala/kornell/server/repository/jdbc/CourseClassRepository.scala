package kornell.server.repository.jdbc

import kornell.server.repository.jdbc.SQLInterpolation.SQLHelper
import java.sql.ResultSet
import kornell.core.entity.CourseClass
import kornell.server.repository.Entities
import kornell.core.entity.Person

class CourseClassRepository(uuid:String) {

  implicit def toCourseClass(rs:ResultSet):CourseClass = 
    Entities.newCourseClass(rs.getString("uuid"), rs.getString("name"),
        rs.getString("courseVersion_uuid"),rs.getString("institution_uuid"))
  
  def get = sql"""
  select * from CourseClass where uuid=$uuid
  """.get[CourseClass]
  
  def version = CourseVersionRepository(get.getCouresVersionUUID())
  
  def actomsVisitedBy(p: Person): List[String] = sql"""
  	select actom_key from ActomEntered 
  	where courseClass_uuid = ${uuid}
  	and person_uuid = ${p.getUUID}
  	order by eventFiredAt
  	""".map[String]({ rs => rs.getString("actom_key") })
  
}

object CourseClassRepository {
  def apply(uuid:String) = new CourseClassRepository(uuid)
}