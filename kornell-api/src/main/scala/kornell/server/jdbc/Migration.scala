package kornell.server.jdbc

import java.util.logging.Logger
import scala.util.Failure
import scala.util.Success
import scala.util.Try
import com.googlecode.flyway.core.Flyway
import java.util.logging.Level

object Migration {
  val log = Logger.getLogger(getClass.getName)

  def apply() = migration

  lazy val migration = migrate match {
    case Success(m) => m
    case Failure(t) => log.log(Level.SEVERE, "Could not migrate database, check your JDBC_* settings.",t)
  }

  def migrate = Try {
    log.info("Starting Database Migration")
    val flyway = new Flyway()
    DataSources.configure(flyway)
    flyway.setLocations("db/jdbcmigration","db/migration") 
    flyway.setOutOfOrder(true)
    flyway.migrate()
  }

  
}