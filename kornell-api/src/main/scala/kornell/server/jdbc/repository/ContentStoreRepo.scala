package kornell.server.jdbc.repository

import scala.language.implicitConversions
import kornell.server.jdbc.SQL._
import java.sql.ResultSet
import kornell.server.repository.Entities
import kornell.core.entity.ContentStore
import scala.language.implicitConversions

class ContentStoreRepo(uuid: String,distributionPrefix:String) {

  implicit def toContentStore(rs: ResultSet) = {
    val cs = Entities.newContentStore(rs.getString("uuid"))    
    val meta = rs.getMetaData()
    for (i <- 1 to meta.getColumnCount()) {
      val colName = meta.getColumnName(i)
      cs.getProperties().put(colName, rs.getString(colName))
    }
    cs.getProperties().put("distributionPrefix", distributionPrefix)
    cs
  }

  lazy val finder = sql"""
     select * from S3ContentRepository where uuid=$uuid
  """
     
  def first = finder.first[ContentStore]
}

object ContentStoreRepo {
  def apply(uuid: String,prefix:String) = new ContentStoreRepo(uuid,prefix)
}