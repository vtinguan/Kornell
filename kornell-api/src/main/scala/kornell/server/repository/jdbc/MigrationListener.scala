package kornell.server.repository.jdbc

import javax.servlet.annotation.WebListener
import javax.servlet.ServletContextListener
import javax.servlet.ServletContextEvent
import com.googlecode.flyway.core.Flyway
import scala.util.Try
import com.googlecode.flyway.core.api.FlywayException

@WebListener
class MigrationListener extends ServletContextListener {
  override def contextInitialized(e: ServletContextEvent): Unit = {
    try {
      val flyway = new Flyway();
      flyway.setDataSource(DataSources.KornellDS)
      flyway.setLocations("db/");
      flyway.migrate();
    } catch {
      case e:FlywayException => {
        //TODO: Fail deployment and notify the world
        e.printStackTrace(System.err)
        //System.exit(-1)
      }
    }
  }

  override def contextDestroyed(e: ServletContextEvent): Unit = {
  }

}