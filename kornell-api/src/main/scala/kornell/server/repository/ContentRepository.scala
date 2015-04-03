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

object ContentRepository {

  def findKNLVisitedContent(courseClassUUID: String, personUUID: String) = {
    val classRepo = CourseClassesRepo(courseClassUUID)
    val visited = classRepo.actomsVisitedBy(personUUID)
    val versionRepo = classRepo.version
    val version = versionRepo.get
    val repositoryUUID = version.getRepositoryUUID
    val repo = S3(repositoryUUID)
    val structureSrc = repo.source(version.getDistributionPrefix(), "structure.knl")
    val structureText = structureSrc.get.mkString("")
    val prefix = repo.prefix + "/" + version.getDistributionPrefix()
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