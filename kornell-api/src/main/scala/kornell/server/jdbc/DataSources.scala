package kornell.server.jdbc

import java.sql.DriverManager
import javax.naming.InitialContext
import javax.naming.Context
import javax.sql.DataSource
import java.util.logging._
import scala.util.Try
import scala.collection.JavaConverters._
import scala.util.Success
import scala.util.Failure
import SQL._
import kornell.server.util.Settings._

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
      .lookup(JNDI_ROOT)
      .asInstanceOf[Context]
    context.lookup(JNDI_DATASOURCE)
      .asInstanceOf[DataSource]
  }

  lazy val JNDI: Try[ConnectionFactory] = Try {
    ping { () => KornellDS.getConnection }
  }

  def getConnection(url: String, user: String, pass: String): Try[ConnectionFactory] = Try {
    ping { () => DriverManager.getConnection(url, user, pass) }
  }

  lazy val LOCAL = getConnection(DEFAULT_URL,DEFAULT_USERNAME,DEFAULT_PASSWORD)


  lazy val SYSPROPS = getConnection(
    prop("JDBC_CONNECTION_STRING"),
    prop("JDBC_USERNAME"),
    prop("JDBC_PASSWORD"))

  //TODO: Consider configuring from imported implicit  (like ExecutionContext)
  val connectionFactory = JNDI.orElse(SYSPROPS).orElse(LOCAL)
  
  connectionFactory match {
    case Success(cf) => log.info("Connection Factory validated. Nice!");
    case Failure(e) => log.severe("Can't live without a databse, sorry :("); //TODO: DIE
  }
  

}