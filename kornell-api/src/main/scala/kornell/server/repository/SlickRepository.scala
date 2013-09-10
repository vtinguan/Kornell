package kornell.server.repository

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.Date
import java.util.UUID
import scala.slick.session.Database
import javax.naming.InitialContext
import javax.sql.DataSource
import javax.naming.NoInitialContextException
import java.sql.DriverManager
import javax.naming.Context
import kornell.core.shared.data.Entity
 
trait SlickRepository {
  def randUUID: String = UUID.randomUUID.toString
  //TODO: Use Connection Pooling from JNDI (have to care for off-container connections, useful in development)
  //SLICK Connections (deprecated)
  def forURL(url: String) = Database.forURL(url,
    driver = "com.mysql.jdbc.Driver",
    user = "kornell",
    password = "42kornell73")

  val nodb = forURL("jdbc:mysql://db.kornell/")

  def db = try Database.forName("java:comp/env/jdbc/KornellDS")
  catch {
    case e: NoInitialContextException => forURL("jdbc:mysql://db.kornell/ebdb")
  }

}

