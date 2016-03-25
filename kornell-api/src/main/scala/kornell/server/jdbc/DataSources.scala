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
import com.zaxxer.hikari.HikariDataSource
import com.googlecode.flyway.core.Flyway

object DataSources {
  val log = Logger.getLogger(getClass.getName)

  def ping(cf: ConnectionFactory, dbDesc: String = ""): Try[ConnectionFactory] = Try {
    log.info(s"Pinging database [$dbDesc]")
    try {
      val conn = cf()
      val stmt = conn.createStatement
      stmt.execute("select 40+2")
      stmt.close
      conn.close
      cf
    } catch {
      case t: Throwable => {
        log.info("Could not connect to [$dbDesc]")
        t.printStackTrace() // TODO: handle error
        throw t
      }
    }
  }

  lazy val kornellDS = {
    val context = new InitialContext()
      .lookup(JNDI_ROOT)
      .asInstanceOf[Context]
    val ds = context.lookup(JNDI_DATASOURCE)
      .asInstanceOf[DataSource]
    ds
  }

  lazy val hikariDS = {
    val ds = new HikariDataSource();
    ds.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource")
    ds.addDataSourceProperty("dataSource.url", prop("JDBC_CONNECTION_STRING"))
    ds.addDataSourceProperty("dataSource.user", prop("JDBC_USERNAME"))
    ds.addDataSourceProperty("dataSource.password", prop("JDBC_PASSWORD"))
    ds.addDataSourceProperty("dataSource.databaseName", "ebdb")
    ds
  }

  lazy val JNDI: Try[ConnectionFactory] =
    ping({ () => kornellDS.getConnection }, s"$JNDI_ROOT/$JNDI_DATASOURCE")

  def getConnection(url: String, user: String, pass: String): Try[ConnectionFactory] =
    ping({ () => DriverManager.getConnection(url, user, pass) }, s"JDBC@$url,$user,$pass")

  lazy val LOCAL = getConnection(DEFAULT_URL, DEFAULT_USERNAME, DEFAULT_PASSWORD)

  lazy val SYSPROPS = getConnection(
    prop("JDBC_CONNECTION_STRING"),
    prop("JDBC_USERNAME"),
    prop("JDBC_PASSWORD"))

  lazy val POOL = ping ({ () => hikariDS.getConnection  },"HikariDS")

  val connectionFactory = POOL orElse JNDI orElse SYSPROPS orElse LOCAL 
  
  def configure(flyway:Flyway) = connectionFactory match {
      case JNDI => flyway.setDataSource(kornellDS)
      case _ => flyway.setDataSource(
          prop("JDBC_CONNECTION_STRING"),
          prop("JDBC_USERNAME"),
          prop("JDBC_PASSWORD")) 
  }

  connectionFactory match {
    case Success(cf) => log.info(s"Connection Factory validated ${connectionFactory} . Nice!");
    case Failure(e) => log.severe("Can't live without a databse, sorry :("); //TODO: DIE
  }

}