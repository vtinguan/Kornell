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
import kornell.server.util.SHA256
 
 object Sandbox extends App {
     println(UUID.random)
     println(UUID.random)
     println(UUID.random)
     println(UUID.random)
     println(UUID.random)
     
     
 		    
     val fos = new FileOutputStream(new File("C://test.pdf"))
     //fos.write(ReportCourseClassGenerator.generateCourseClassReport("dd342fea-3210-4cb6-84f1-0b91fc4173dd", "pdf"))
     fos.close()
     System.out.println("fine")
 }