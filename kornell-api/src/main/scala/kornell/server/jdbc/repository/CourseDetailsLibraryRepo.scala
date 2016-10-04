package kornell.server.jdbc.repository

import java.sql.ResultSet
import kornell.server.jdbc.SQL._ 
import kornell.core.entity.CourseDetailsLibrary


class CourseDetailsLibraryRepo(uuid: String) {

  val finder = sql"select * from CourseDetailsLibrary where uuid=$uuid"

  def get = finder.get[CourseDetailsLibrary]
  def first = finder.first[CourseDetailsLibrary]
  
  def update(courseDetailsLibrary: CourseDetailsLibrary): CourseDetailsLibrary = {    
    sql"""
    | update CourseDetailsLibrary l
    | set l.title = ${courseDetailsLibrary.getTitle},
    | l.index = ${courseDetailsLibrary.getIndex}, 
    | l.description = ${courseDetailsLibrary.getDescription},
    | l.size = ${courseDetailsLibrary.getSize},
    | l.path = ${courseDetailsLibrary.getPath},
    | l.uploadDate = ${courseDetailsLibrary.getUploadDate},
    | l.fontAwesomeClassName = ${courseDetailsLibrary.getFontAwesomeClassName}
    | where l.uuid = ${courseDetailsLibrary.getUUID}""".executeUpdate
    
    courseDetailsLibrary
  }

}

object CourseDetailsLibraryRepo {
  def apply(uuid: String) = new CourseDetailsLibraryRepo(uuid)
}