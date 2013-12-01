package kornell.server.repository.jdbc

import javax.servlet.annotation.WebListener
import javax.servlet.ServletContextListener
import javax.servlet.ServletContextEvent
import com.googlecode.flyway.core.Flyway
import scala.util.Try
import com.googlecode.flyway.core.api.FlywayException

@WebListener
class MigrationListener extends ServletContextListener {
  override def contextInitialized(e: ServletContextEvent): Unit = Migration()
  
  override def contextDestroyed(e: ServletContextEvent): Unit = {}

}