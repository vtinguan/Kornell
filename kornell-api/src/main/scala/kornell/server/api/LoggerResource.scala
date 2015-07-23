package kornell.server.api

import javax.ws.rs.Path
import javax.ws.rs.GET
import javax.ws.rs.Produces
import scala.collection.mutable.StringBuilder
import java.util.logging._
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

@Path("log")
class LoggerResource {
    val logger = Logger.getLogger(classOf[LoggerResource].getName)
    val df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    
     @GET
     @Produces(Array("text/plain"))
     def log:String = {
       val out = new StringBuilder
       val tag:String = df.format(new Date)
       logger.finest(s"--- finest --- $tag")
       logger.fine(s"--- fine --- $tag")
       logger.finer(s"--- finer --- $tag")
       logger.info(s"--- info ---")
       logger.severe(s"--- severe --- $tag")
       logger.warning(s"--- warning --- $tag")
       println(s"--- console --- $tag")
       out.append(s"Messages logged $tag")
       out.toString       
     }
    
     @GET
     @Path("throws")
     @Produces(Array("text/plain"))
     def throws = throw new RuntimeException("Ops!")
}