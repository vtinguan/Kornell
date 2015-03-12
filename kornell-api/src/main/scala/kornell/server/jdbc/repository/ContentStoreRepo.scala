package kornell.server.jdbc.repository

import scala.language.implicitConversions
import kornell.server.jdbc.SQL._
import java.sql.ResultSet
import kornell.server.repository.Entities
import kornell.core.entity.ContentStore
import scala.language.implicitConversions
import kornell.core.entity.ContentStoreType

class ContentStoreRepo {

  implicit def toContentStore(rs: ResultSet): ContentStore = {
    val cs = Entities.newContentStore(rs.getString("uuid"))
    val meta = rs.getMetaData()
    for (i <- 1 to meta.getColumnCount()) {
      val colName = meta.getColumnName(i)
      cs.getProperties().put(colName, rs.getString(colName))
    }
    cs.setContentStoreType(ContentStoreType.valueOf(rs.getString("contentStoreType")))
    cs
  }

  def s3Finder(uuid: String) = sql"""
     select *, 'S3' as contentStoreType  from S3ContentStore where uuid=$uuid
  """

  def fsFinder(uuid: String) = sql"""
     select *, 'FS' as contentStoreType from FSContentStore where uuid=$uuid
  """

  def first(uuid: String) = s3Finder(uuid)
    .first[ContentStore]
    .orElse(fsFinder(uuid)
      .first[ContentStore])

  def get(uuid: String) = first(uuid).get
}