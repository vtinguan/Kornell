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
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import kornell.server.jdbc.repository.ContentStoreRepo
import kornell.server.content.ContentManagers
import kornell.server.jdbc.repository.CourseClassRepo

@ApplicationScoped
class ContentRepository @Inject() (
  cms:ContentManagers,
  courseClassesRepo:CourseClassesRepo
  ) {
  
  def this() = this(null,null)

  def findKNLVisitedContent(courseClassUUID: String, personUUID: String) = {
    val classRepo = courseClassesRepo.byUUID(courseClassUUID)
    val visited = classRepo.actomsVisitedBy(personUUID)
    val versionRepo = classRepo.version
    val cv = versionRepo.get
    val cm = cms.forCourseVersion(cv)
    val structureSrc = cm.source("structure.knl").get
    val structureText = structureSrc.mkString("")
    val baseURL = "????DEPRECATED????"
    val contents = ContentsParser.parse(baseURL, "????DEPRECATED????", structureText, visited)
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
    val classRepo = courseClassesRepo.byUUID(courseClassUUID)
    val versionRepo = classRepo.version
    val version = versionRepo.get
    val cm = cms.forCourseVersion(version)
    val structureIn = cm.getObjectStream ("imsmanifest.xml").get
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