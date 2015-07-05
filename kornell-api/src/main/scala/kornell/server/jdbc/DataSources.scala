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

object DataSources { 
  val log = Logger.getLogger(getClass.getName)

  def ping(cf: ConnectionFactory,dbDesc:String=""): Try[ConnectionFactory] = Try {
    log.info(s"Pinging database [$dbDesc]")
    try{
      val conn = cf()
      val stmt = conn.createStatement
      stmt.execute("select 40+2")
      stmt.close
      conn.close    
      cf
    }catch {
      case t: Throwable => {
        log.info("Could not connect to [$dbDesc]")
        t.printStackTrace() // TODO: handle error
        throw t
      }
    }
  }

  lazy val KornellDS = {
    val context = new InitialContext()
      .lookup(JNDI_ROOT)
      .asInstanceOf[Context]
    val ds = context.lookup(JNDI_DATASOURCE)
      .asInstanceOf[DataSource]
    ds
  }

  lazy val JNDI: Try[ConnectionFactory] =    
    ping ({ () => KornellDS.getConnection }, s"$JNDI_ROOT/$JNDI_DATASOURCE")
  

  def getConnection(url: String, user: String, pass: String): Try[ConnectionFactory] =     
    ping ({ () => DriverManager.getConnection(url, user, pass) },s"JDBC@$url,$user,$pass")
  

  lazy val LOCAL = getConnection(DEFAULT_URL,DEFAULT_USERNAME,DEFAULT_PASSWORD)


  lazy val SYSPROPS = getConnection(
    prop("JDBC_CONNECTION_STRING"),
    prop("JDBC_USERNAME"),
    prop("JDBC_PASSWORD"))

  //TODO: Consider configuring from imported implicit  (like ExecutionContext)
  val connectionFactory = JNDI.orElse(SYSPROPS).orElse(LOCAL)
  
  connectionFactory match {
    case Success(cf) => log.info(s"Connection Factory validated ${connectionFactory} . Nice!");
    case Failure(e) => log.severe("Can't live without a databse, sorry :("); //TODO: DIE
  }
  

}