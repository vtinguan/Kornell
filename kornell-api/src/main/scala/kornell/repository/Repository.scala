package kornell.repository

import java.sql.Connection
import java.sql.{ Date => SQLDate }
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.Date
import java.util.UUID
import scala.slick.session.Database
import javax.naming.InitialContext
import javax.sql.DataSource
import javax.naming.NoInitialContextException
import java.sql.DriverManager
import javax.naming.Context

trait Repository {
  def randUUID: String = UUID.randomUUID.toString
  //TODO: Use Connection Pooling from JNDI (have to care for off-container connections, useful in development)

  //SLICK Connections (deprecated)
  def forURL(url: String) = Database.forURL(url,
    driver = "com.mysql.jdbc.Driver",
    user = "kornell",
    password = "42kornell73")

  val nodb = forURL("jdbc:mysql://db.kornell/")

  def db = try Database.forName("java:comp/env/jdbc/KornellDS")
  catch {
    case e: NoInitialContextException => forURL("jdbc:mysql://db.kornell/ebdb")
  }

  //TODO: Support named parameters
  def update(sql: String, params: List[Any]) = new Statement(sql).executeUpdate(params)

  implicit def toStatement(sql: String) = new Statement(sql)
}

class Statement(sql: String) {
  //JDBC Connections (FTW!)
  val ctx = try
    Option(new InitialContext().lookup("java:comp/env").asInstanceOf[Context])
  catch { case e: NoInitialContextException => None }

  //TODO: Try localhost if db.kornell is not found.
  def connect =
    if (ctx.isEmpty)
      DriverManager.getConnection("jdbc:mysql://db.kornell/ebdb", "kornell", "42kornell73")
    else ctx
      .get
      .lookup("jdbc/KornellDS")
      .asInstanceOf[DataSource]
      .getConnection

  def connected[T](fun: Connection => T): Option[T] = {
    val conn = connect
    try Option(fun(conn))
    finally conn.close
  }

  def prepared(params: List[Any])(fun: PreparedStatement => Unit) =
    connected { conn =>
      val pstmt = conn.prepareStatement(sql.stripMargin.trim)
      //FTW: High Order Functions with Pattern Matching
      params
        .zipWithIndex
        .map { case (p, i) => (p, i + 1) } //JDBC indexes starts on 1 
        .foreach {
          case (p: String, i) => pstmt.setString(i, p)
          case (p: Integer, i) => pstmt.setInt(i, p)
          case (p: Double, i) => pstmt.setDouble(i, p)
          case (p: Date, i) => pstmt.setDate(i, new SQLDate(p.getTime))
          case (p, i) => throw new RuntimeException(s"How should i set param ($p:${p.getClass})")
        }
      try fun(pstmt)
      finally pstmt.close
    }
  
  def query(params: Any*)(fun: ResultSet => Unit) =
    prepared(params.toList) { stmt =>
      val rs = stmt.executeQuery
      try fun(rs)
      finally rs.close
    }
  
  def executeUpdate(params: List[Any]) = prepared(params)({ stmt => stmt.executeUpdate })
}

