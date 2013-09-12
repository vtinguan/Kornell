package kornell.server.repository.jdbc

import java.sql.DriverManager
import javax.naming.NoInitialContextException
import javax.naming.InitialContext
import javax.naming.Context
import javax.sql.DataSource
import java.util.logging._

object DataSources {
  val log = Logger.getLogger(getClass.getName)
    
  lazy val KornellDS = {
    val context = new InitialContext()
      .lookup("java:comp/env")
      .asInstanceOf[Context]
    context.lookup("jdbc/KornellDS")
      .asInstanceOf[DataSource]
  }

  lazy val JNDI: ConnectionFactory = () => {
    try {
      KornellDS.getConnection
    } catch {
      case e: NoInitialContextException => null
    }
  }

  lazy val LOCAL: ConnectionFactory = () =>
    DriverManager.getConnection("jdbc:mysql:///ebdb", "kornell", "42kornell73")

  lazy val connect: ConnectionFactory = verified(JNDI).getOrElse(LOCAL)

  def verified(cf: ConnectionFactory): Option[ConnectionFactory] =
    try {      
      val conn = cf()
      val stmt = conn.createStatement
      stmt.execute("select 40+2")
      stmt.close
      conn.close
      log.info("Connection Factory validated. Nice!");
      Some(cf)
    } catch { case e: Exception => None }
}