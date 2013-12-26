import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.Signature
import java.security.spec.X509EncodedKeySpec
import java.security.KeyFactory
import kornell.server.repository.jdbc.SQLInterpolation._
import scala.util._
import scala.util.Try

object sandbox {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(368); val res$0 = 



  System.setProperty("JDBC_CONNECTION_STRING", "jdbc:mysql:///uala");System.out.println("""res0: String = """ + $show(res$0));$skip(46); val res$1 = 
  System.setProperty("JDBC_USERNAME", "root");System.out.println("""res1: String = """ + $show(res$1));$skip(51); val res$2 = 
  System.setProperty("JDBC_PASSWORD", "masterkey");System.out.println("""res2: String = """ + $show(res$2));$skip(50); 
  val name = "uala " + System.currentTimeMillis();System.out.println("""name  : String = """ + $show(name ));$skip(59); val res$3 = 
	sql"insert into sluck(name) values ($name)".executeUpdate;System.out.println("""res3: Int = """ + $show(res$3))}
}
