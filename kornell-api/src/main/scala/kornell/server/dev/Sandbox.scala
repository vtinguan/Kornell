package kornell.server.dev
 
 import kornell.core.util.UUID
import kornell.server.report.ReportGenerator
import scala.io.Source
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import java.io.FileInputStream
import javax.xml.xpath.XPathFactory
import javax.xml.xpath.XPathConstants
import org.w3c.dom.NodeList
import java.io.FileOutputStream
import java.io.File
import kornell.server.report.ReportCourseClassGenerator
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
 
 object Sandbox extends App {
     val creds = new BasicAWSCredentials("AKIAJTTDCQYWZLJYGSJA","NUIXMkP3NBY+lLHHohEZDbnfs/ewCMgaYpQnL2sv")
     val s3 = new AmazonS3Client(creds)
     val obj = s3.getObject("unicc",  "repository/42df235e-a2e8-455b-b341-84b4f8e5c88b/nr-17/v0.1/imsmanifest.xml")
     println(obj.getKey());
   
     
   
   
 }