package kornell.server.web

import javax.servlet.ServletContextListener
import kornell.repository.JDBCRepository
import javax.servlet.ServletContextEvent
import javax.servlet.annotation.WebListener

@WebListener
class DatabaseCreator extends ServletContextListener with JDBCRepository {
  def contextInitialized(evt:ServletContextEvent) = {    
  }
  

  def contextDestroyed(evt:ServletContextEvent) = {}
  
}

