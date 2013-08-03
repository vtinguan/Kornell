package kornell.repository

import java.sql.DriverManager
import javax.naming.InitialContext
import java.sql.Connection
import javax.naming.Context
import java.util.Date
import javax.sql.DataSource
import java.sql.PreparedStatement
import java.sql.{ Date => SQLDate }

trait JDBCRepository {
  implicit def toStatement(sql: String) = new Statement(sql)
}

class Statement(sql: String) {
  type ConnectionFactory = Function0[Connection]

  val connect: ConnectionFactory =
    tryJNDI.getOrElse(
      tryRemote.getOrElse(
        tryLocal.get))

  def validated(connect: ConnectionFactory): Option[ConnectionFactory] = try {
    val conn = connect()
    Some(connect)
  } catch {
    case e: Throwable => { /*e.printStackTrace();*/ None }
  }

  def tryJNDI: Option[ConnectionFactory] = {
    validated({
      def cff = {
        //TODO: Consider the scope/caching of InitialContext
        val context = new InitialContext()
          .lookup("java:comp/env")
          .asInstanceOf[Context]
        context.lookup("jdbc/KornellDS")
          .asInstanceOf[DataSource]
          .getConnection
      }
      cff _
    })
  }

  def tryRemote: Option[ConnectionFactory] =
    validated({
      def cff = { DriverManager.getConnection("jdbc:mysql://db.kornell/ebdb", "kornell", "42kornell73") }
      cff _
    })

  def tryLocal: Option[ConnectionFactory] =
    validated({
      def cff = { DriverManager.getConnection("jdbc:mysql://localhost/ebdb", "kornell", "42kornell73") }
      cff _
    })

  def connected[T](fun: Connection => T): T = {
    val conn = connect()
    try fun(conn)
    finally conn.close
  }

  def prepared(params: List[Any])(fun: PreparedStatement => Unit): List[Any] =
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
          //TODO: make this work: case (p: Entity, i) => pstmt.setString(i, p.getUUID) 
          case (p, i) => throw new RuntimeException(s"How should i set param ($p:${p.getClass})")
        }
      try fun(pstmt)
      finally pstmt.close
      params
    }

  def execute() = prepared(List())({ stmt => stmt.execute })
  def executeUpdate(params: List[Any]): List[Any] = prepared(params)({ stmt => stmt.executeUpdate })
}