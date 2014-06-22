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
    println("[SYSOUT] Starting Database Mygration")
    log.info("[INFO] Starting Database Mygration")
    val flyway = new Flyway();
    setDataSource(flyway)
    flyway.setLocations("db/");
    flyway.migrate();
  }

  //TODO: Clean this ugly hack moving it to a "DataSources" method
  private def setDataSource(flyway: com.googlecode.flyway.core.Flyway): Unit =
    DataSources.JNDI match {
      case Success(_) => {
        log.info("Using JNDI as a target for migrations")
        flyway.setDataSource(DataSources.KornellDS)
      }
      case Failure(_) => {
        log.info("Using LOCAL as a target for migration")
        DataSources.SYSPROPS match {
          case Success(_) => flyway.setDataSource(
            prop("JDBC_CONNECTION_STRING"),
            prop("JDBC_USERNAME"),
            prop("JDBC_PASSWORD"))
          case Failure(_) => flyway.setDataSource(DEFAULT_URL, DEFAULT_USERNAME, DEFAULT_PASSWORD)
        }
      }
    }
}