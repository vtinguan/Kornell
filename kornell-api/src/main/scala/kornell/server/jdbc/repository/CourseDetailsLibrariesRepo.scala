package kornell.server.jdbc.repository

import java.sql.ResultSet
import scala.collection.JavaConverters._
import kornell.server.jdbc.SQL._
import kornell.core.util.UUID
import kornell.core.entity.CourseDetailsLibrary

object CourseDetailsLibrariesRepo {

  def create(courseDetailsLibrary: CourseDetailsLibrary): CourseDetailsLibrary = {
    if (courseDetailsLibrary.getUUID == null){
      courseDetailsLibrary.setUUID(UUID.random)
    }    
    sql"""
    | insert into CourseDetailsLibrary (uuid,title,entityType,entityUUID,`index`,description,size,path,uploadDate,fontAwesomeClassName) 
    | values(
    | ${courseDetailsLibrary.getUUID},
    | ${courseDetailsLibrary.getTitle},
    | ${courseDetailsLibrary.getEntityType.toString}, 
    | ${courseDetailsLibrary.getEntityUUID},
    | ${courseDetailsLibrary.getIndex},
    | ${courseDetailsLibrary.getDescription},
    | ${courseDetailsLibrary.getSize},
    | ${courseDetailsLibrary.getPath},
    | ${courseDetailsLibrary.getUploadDate},
    | ${courseDetailsLibrary.getFontAwesomeClassName})""".executeUpdate
    
    courseDetailsLibrary
  }  
}