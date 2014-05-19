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

object Sandbox extends App {
    println(UUID.random)
    println(UUID.random)
    println(UUID.random)
    println(UUID.random)
    println(UUID.random)
    
    //ReportGenerator.generateCertificate2("839D06CA-6297-40E8-A39D-82DBED7970D9")
    ReportGenerator.generateCertificateByCourseClass("dd342fea-3210-4cb6-84f1-0b91fc4173dd")
  
    
  
  
}