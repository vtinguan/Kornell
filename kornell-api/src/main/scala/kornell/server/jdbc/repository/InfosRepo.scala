package kornell.server.jdbc.repository

import java.sql.ResultSet
import kornell.server.jdbc.SQL._
import kornell.server.repository.TOs
import kornell.core.to.InfoTO
import scala.collection.JavaConverters._
import scala.language.postfixOps
import scala.language.implicitConversions

object InfosRepo {
  implicit def toInfoTO(rs: ResultSet): InfoTO = TOs.newInfoTO(
    rs.getString("uuid"),
    rs.getString("courseVersionUUID"),
    rs.getString("category"),
    rs.getString("subcategory"),
    rs.getInt("sequence"),
    rs.getString("title"),
    rs.getString("text"))

  def byCourseVersion(courseVersionUUID: String) = {
    val infoTOs = sql"""
  	SELECT uuid, courseVersionUUID,
  		category,subcategory,sequence,
  		title,text from CourseVersionInfo
  """.map[InfoTO]
    val infosMap = infoTOs
      .sortBy(_.getSequence)
      .groupBy(_.getCategory)
      .mapValues(l => l asJava)
    TOs.newInfosTO(infosMap)    
  }

}