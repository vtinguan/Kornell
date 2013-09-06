package kornell.server.repository.jdbc

import javax.servlet.annotation.WebListener
import javax.servlet.ServletContextListener
import javax.servlet.ServletContextEvent
import com.googlecode.flyway.core.Flyway

@WebListener
class MigrationListener extends ServletContextListener {
  override def contextInitialized(e: ServletContextEvent): Unit = {
    val flyway = new Flyway();
    flyway.setDataSource(DataSources.KornellDS)
    flyway.setLocations("db/");
    flyway.migrate();
  }

  override def contextDestroyed(e: ServletContextEvent): Unit = {
  }

}