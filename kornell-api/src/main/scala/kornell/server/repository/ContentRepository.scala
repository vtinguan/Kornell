package kornell.server.repository

import kornell.server.dev.util.ContentsParser
import kornell.server.repository.s3.S3
import kornell.server.jdbc.repository.CourseClassesRepo
import kornell.core.entity.Person
import javax.xml.parsers.DocumentBuilderFactory
import scala.collection.mutable.ListBuffer
import javax.xml.xpath.XPathFactory
import org.w3c.dom.NodeList
import javax.xml.xpath.XPathConstants
import java.io.ByteArrayInputStream
import kornell.core.util.StringUtils
import kornell.server.jdbc.repository.PersonRepo
import kornell.core.entity.Enrollment
import kornell.server.jdbc.repository.CourseVersionRepo

object ContentRepository {

  def findKNLVisitedContent(enrollment: Enrollment) = {
    val personRepo = PersonRepo(enrollment.getPersonUUID)
    val visited = personRepo.actomsVisitedBy(enrollment.getUUID)    
    val version = {
      if(enrollment.getCourseVersionUUID != null)
        CourseVersionRepo(enrollment.getCourseVersionUUID)
       else 
        CourseClassesRepo(enrollment.getCourseClassUUID).version
    }.get
    val repositoryUUID = version.getRepositoryUUID
    val repo = S3(repositoryUUID)
    val x = version.getDistributionPrefix + "structure.knl"
    val structureSrc = repo.source(version.getDistributionPrefix, "structure.knl")
    val structureText = structureSrc.get.mkString("")
    val prefix = StringUtils.mkurl(repo.prefix, version.getDistributionPrefix())
    val contents = ContentsParser.parse(prefix, structureText, visited)
    contents
  }

  val expression = "//resource/@href"
  lazy val xPath = XPathFactory.newInstance().newXPath()
  lazy val expr = xPath.compile(expression)
  
  def findSCORM12Actoms(courseClassUUID: String) = {
    /* i wish they were thread safe */
    val builderFactory = DocumentBuilderFactory.newInstance
    val builder = builderFactory.newDocumentBuilder
    /* </rant> */
    val classRepo = CourseClassesRepo(courseClassUUID)
    val versionRepo = classRepo.version
    val version = versionRepo.get
    val repositoryUUID = version.getRepositoryUUID();
    val repo = S3(repositoryUUID)
    val structureIn = repo.inputStream(version.getDistributionPrefix(), "imsmanifest.xml").get
    val document = builder.parse(structureIn)
    val result = ListBuffer[String]()
    val nodes: NodeList = expr.evaluate(document, XPathConstants.NODESET).asInstanceOf[NodeList]
    for (i <- 0 until nodes.getLength) {
      result += nodes.item(i).getFirstChild.getNodeValue
    }
    structureIn.close
    result.toList
  }
}