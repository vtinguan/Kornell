package kornell.server.repository.jdbc

import kornell.server.repository.jdbc.SQLInterpolation.SQLHelper
import java.sql.ResultSet
import kornell.server.repository.Entities
import kornell.core.entity.CourseVersion

class CourseVersionRepository(uuid:String) {
    implicit def toCourseVersion(rs:ResultSet):CourseVersion =
      Entities.newCourseVersion(rs.getString("uuid"),rs.getString("name"),
          rs.getString("course_uuid"),rs.getString("repository_uuid"))
      
	def get = sql"""
		select * from CourseVersion where uuid=$uuid
	""".get[CourseVersion]
}

object CourseVersionRepository{
  def apply(uuid:String) = new CourseVersionRepository(uuid:String) 
}