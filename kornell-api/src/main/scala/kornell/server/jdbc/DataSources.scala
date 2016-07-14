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
import com.zaxxer.hikari.HikariConfig
import kornell.server.util.Settings._

object DataSources {
  val log = Logger.getLogger(getClass.getName)

  lazy val hikariDS = {
    val driverName = JDBC_DRIVER.get
    val jdbcURL = JDBC_CONNECTION_STRING.get
    val username = JDBC_USERNAME.get
    val password = JDBC_PASSWORD.get
    log.info(s"JDBC properties [$driverName, $jdbcURL, $username, *****]")
    val config = new HikariConfig()
    config.setDriverClassName(driverName)
    config.setJdbcUrl(jdbcURL)
    config.setUsername(username)
    config.setPassword(password)
    config.addDataSourceProperty("characterEncoding","utf8");
    config.addDataSourceProperty("useUnicode","true");
    val ds = new HikariDataSource(config)
    ds
  }


  lazy val POOL = { () => hikariDS.getConnection  }

  val connectionFactory = POOL 
  
  def configure(flyway:Flyway) = flyway.setDataSource(
          JDBC_CONNECTION_STRING,
          JDBC_USERNAME,
          JDBC_PASSWORD)
}