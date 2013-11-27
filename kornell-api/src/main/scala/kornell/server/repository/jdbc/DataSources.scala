package kornell.server.repository.jdbc

import java.sql.DriverManager
import javax.naming.NoInitialContextException
import javax.naming.InitialContext
import javax.naming.Context
import javax.sql.DataSource
import java.util.logging._
import scala.util.Try
import scala.collection.JavaConverters._
import scala.util.Success
import scala.util.Failure

object DataSources {
  val log = Logger.getLogger(getClass.getName)

  def ping(cf: ConnectionFactory): ConnectionFactory = {
    val conn = cf()
    val stmt = conn.createStatement
    stmt.execute("select 40+2")
    stmt.close
    conn.close    
    cf
  }

  lazy val KornellDS = {
    val context = new InitialContext()
      .lookup("java:comp/env")
      .asInstanceOf[Context]
    context.lookup("jdbc/KornellDS")
      .asInstanceOf[DataSource]
  }

  lazy val JNDI: Try[ConnectionFactory] = Try {
    ping { () => KornellDS.getConnection }
  }

  def getConnection(url: String, user: String, pass: String): Try[ConnectionFactory] = Try {
    ping { () => DriverManager.getConnection(url, user, pass) }
  }

  lazy val LOCAL = getConnection("jdbc:mysql:///ebdb", "kornell", "42kornell73")

  def prop(name: String) = System.getProperty(name)

  lazy val SYSPROPS = getConnection(
    prop("JDBC_CONNECTION_STRING"),
    prop("JDBC_USERNAME"),
    prop("JDBC_PASSWORD"))

  val connectionFactory = JNDI.orElse(SYSPROPS).orElse(LOCAL)
  
  connectionFactory match {
    case Success(cf) => log.info("Connection Factory validated. Nice!");
    case Failure(e) => log.severe("Can't live without a databse, sorry :("); //TODO: DIE
  }
  

}