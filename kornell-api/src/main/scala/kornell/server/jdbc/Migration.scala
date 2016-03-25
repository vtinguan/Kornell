package kornell.server.jdbc

import java.util.logging.Logger

import scala.util.Failure
import scala.util.Success
import scala.util.Try

import com.googlecode.flyway.core.Flyway

object Migration {
  val log = Logger.getLogger(getClass.getName)

  def apply() = migration

  lazy val migration = migrate match {
    case Success(m) => m
    case Failure(t) => throw t
  }

  def migrate = Try {
    println("[SYSOUT] Starting Database Migration")
    log.info("[INFO] Starting Database Migration")
    val flyway = new Flyway()
    DataSources.configure(flyway)
    flyway.setLocations("db/jdbcmigration","db/migration") 
    flyway.setOutOfOrder(true)
    flyway.migrate()
  }

  
}