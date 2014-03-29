package kornell.server.jdbc

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Timestamp
import java.sql.{Date => SQLDate}
import java.util.{Date => JDate}
import kornell.core.value.{Date => KDate}
import java.math.BigDecimal
import scala.collection.mutable.ListBuffer
import DataSources._
import kornell.server.util.Settings
import java.sql.Date
import kornell.core.util.TimeUtil

class PreparedStmt(query: String, params: List[Any]) {

  def connected[T](fun: Connection => T): T = {
    val conn = connectionFactory.get()
    try fun(conn)
    finally conn.close
  }

  def prepared[T](fun: PreparedStatement => T): T =
    connected { conn =>
      val pstmt = conn.prepareStatement(query.stripMargin.trim)

      def setQueryParam(param: Tuple2[Any, Int]) = param match {
        case (null,i) => pstmt.setObject(i+1,null)
        case (p: String, i) => pstmt.setString(i + 1, p)
        case (p: Integer, i) => pstmt.setInt(i + 1, p)
        case (p: Double, i) => pstmt.setDouble(i + 1, p)
        case (p: JDate, i) => pstmt.setTimestamp(i + 1, new Timestamp(p.getTime))
        case (p: KDate, i) => pstmt.setDate(i + 1, new SQLDate(TimeUtil.toJUD(p).getTime()) )
        case (p: BigDecimal, i) => pstmt.setBigDecimal(i + 1, p)
        case (p: Boolean, i) => pstmt.setBoolean(i + 1, p)
        //TODO: make this work: case (p: Entity, i) => pstmt.setString(i, p.getUUID) 
        case (p, i) => throw new IllegalArgumentException(s"Can not set param [$p]")
      }

      params
        .zipWithIndex
        .foreach(setQueryParam)   
      
      logger.finer(s"Executing query [$query].")        
        
      try fun(pstmt)
      finally pstmt.close
    }

  def executeUpdate: Int = prepared { stmt => stmt.executeUpdate }

  
  def executeQuery[T](fun: ResultSet => T ): T = prepared { stmt =>
    val rs = stmt.executeQuery
    try fun(rs)
    finally rs.close
  }
  
  def executeQuery:Unit = executeQuery{ rs => () }

  def foreach(fun: ResultSet => Unit): Unit = executeQuery { rs =>
    while (rs.next) fun(rs)
  }

  //TODO: Consider converting to a Stream for lazy processing
  def map[T](implicit fun: ResultSet => T): List[T] = {
    val xs: ListBuffer[T] = ListBuffer()
    foreach { rs => xs += fun(rs) }
    xs.toList
  }

  def first[T](implicit conversion: ResultSet => T): Option[T] = prepared { stmt =>
    stmt.setMaxRows(1)
    val rs = stmt.executeQuery
    if (!rs.next) None
    else
      try Option(conversion(rs))
      finally rs.close
  }

  def isPositive: Boolean = executeQuery { rs =>
    rs.next && rs.getInt(1) > 0
  }
  
  def getUUID = executeQuery { rs =>
    if (rs.next) rs.getString("uuid")
    else throw new IllegalStateException("Can not get on empty result")
  }
  
  def get[T](implicit conversion: ResultSet => T): T = first(conversion).get
  
  override def toString = query
} 
