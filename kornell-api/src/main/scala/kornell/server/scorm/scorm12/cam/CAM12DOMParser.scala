package kornell.server.scorm.scorm12.cam

import scala.language.implicitConversions
import kornell.core.scorm.scorm12.cam.Manifest
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory
import CAM12._
import org.w3c.dom._
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConverters._
import java.util.List
import java.lang.Boolean
import java.util.List
import kornell.core.scorm.scorm12.cam._
import kornell.core.scorm.scorm12.cam.adlcp._
import kornell.core.scorm.scorm12.cam.adlcp.TimeLimitAction._
import kornell.core.scorm.scorm12.cam.imsmd.LOM
import kornell.core.scorm.scorm12.cam.imsmd.General
import kornell.core.scorm.scorm12.cam.imsmd.LangString
import kornell.core.scorm.scorm12.cam.imsmd.Structure

object CAM12DOMParser {

  implicit class PimpNodeList(nl: NodeList) extends Traversable[Node] {
    override def foreach[U](f: Node => U): Unit = for (i <- 0 until nl.getLength) f(nl.item(i))
  }

  implicit class PimpElement(el: Element) {
    def getChildElements = el.getChildNodes filter isElement  
  }

  val dbf = DocumentBuilderFactory.newInstance
  val db = dbf.newDocumentBuilder

  def isElement(n: Node) = n != null && Node.ELEMENT_NODE.equals(n.getNodeType)

  implicit def toElement(n: Node): Element = if (isElement(n))
    n.asInstanceOf[Element]
  else throw new IllegalArgumentException

  def parse(in: InputStream): Manifest = {
    val doc = db.parse(in)
    doc.getDocumentElement().normalize();
    parseManifest(doc.getDocumentElement)
  }

  def parseManifest(el: Element): Manifest = {
    val manifest = newManifest
    for (child <- el.getChildElements)
      child.getTagName() match {
        case "organizations" => manifest.setOrganizations(parseOrganizations(child))
        case "resources" => manifest.setResources(parseResources(child))
        case "metadata" => manifest.setMetadata(parseMetadata(child))
        case other: String => throw new IllegalStateException(s"Unknown tag <${other}>")
      }
    manifest
  }

  def parseResources(el: Element): Resources = {
    val res = newResources()
    for (child <- el.getChildElements)
      child.getTagName match {
        case "resource" => res.getResourceList.add(parseResource(child))
      }
    res
  }

  def parseResource(el: Element): Resource = {
    val res = newResource()
    res.setIdentifier(el.getAttribute("identifier"))
    res.setType(el.getAttribute("type"))
    res.setScormType(el.getAttribute("adlcp:scormtype"))
    res.setHref(el.getAttribute("href"))
    for (child <- el.getChildElements)
      child.getTagName match {
        case "metadata" => res.setMetadata(parseMetadata(child))
        case "file" => res.getFiles.add(parseFile(child))
        case "dependency" => res.getDependencies().add(parseDependency(child))
      }
    res
  }

  def parseDependency(el: Element) = el.getAttribute("dependency")

  def parseFile(el: Element) = {
    val file = newFile()
    file.setHref(el.getAttribute("href"))
    file
  }

  def parseOrganizations(el: Element): Organizations = {
    val orgs = newOrganizations()
    orgs.setDefaultOrganization(el.getAttribute("default"))
    for (child <- el.getChildElements)
      child.getTagName match {
        case "organization" => orgs.getOrganizationList().add(parseOrganization(child))
      }
    orgs
  }

  def parseOrganization(el: Element): Organization = {
    val org = newOrganization()
    org.setIdentifier(el.getAttribute("identifier"))
    for (child <- el.getChildElements)
      child.getTagName match {
        case "title" => org.setTitle(child.getTextContent)
        case "item" => addItem(org, parseItem(child))
        case "metadata" => org.setMetadata(parseMetadata(child))
        case tag: String => println(s"Unkown tag <${tag}>")
      }
    org
  }

  def addItem(hasItems: HasItems, item: Item) =
    if (hasItems.getItems != null)
      hasItems.getItems.add(item)
    else
      hasItems.setItems(ListBuffer(item).asJava)

  def getChildtext(el: Element, tag: String): String = {
    val children = el.getElementsByTagName(tag)
    if (children.getLength == 0) null else {
      val el: Element = toElement(children.item(0))
      el.getTextContent
    }
  }

  def getBoolean(el: Element, attr: String) = {
    val bool = el.getAttribute(attr)
    if (bool == null) null
    else Boolean.valueOf(bool)
  }

  def parseTimeLimitAction(el: Element): TimeLimitAction = el.getTextContent match {
    case "exit,message" => exit_message
    case "exit,no message" => exit_no_message
    case "continue,message" => continue_message
    case "continue,no message" => continue_no_message
    case _ => unknown_action
  }

  def parsePreRequisits(el: Element): PreRequisites =
    newPreRequisites(el.getTextContent, el.getAttribute("type"))

  def parseItem(el: Element): Item = {
    val item = newItem()
    item.setIdentifier(el.getAttribute("identifier"))
    item.setIdentifierRef(el.getAttribute("identifierref"))
    item.setVisible(getBoolean(el, "isvisible"))

    for (child <- el.getChildElements)
      child.getTagName match {
        case "title" => item.setTitle(child.getTextContent())
        case "item" => addItem(item, parseItem(child))
        case "adlcp:prerequisites" => item.setPreRequisites(parsePreRequisits(child))
        case "adlcp:maxtimeallowed" => item.setMaxTimeAllowed(child.getTextContent)
        case "adlcp:timelimitaction" => item.setTimeLimitAction(parseTimeLimitAction(child))
        case "adlcp:masteryscore" => item.setMasteryScore(child.getTextContent)
        case "metadata" => item.setMetadata(parseMetadata(child))
      }
    item
  }

  def parseMetadata(el: Element): Metadata = {
    val meta = newMetadata
    for (child <- el.getChildElements)
      child.getTagName match {
        case "schema" => meta.setSchema(child.getTextContent)
        case "schemaversion" => meta.setSchemaVersion(child.getTextContent)
        case "adlcp:location" => meta.setLocation(child.getTextContent)
        case "imsmd:lom" => meta.setLOM(parseLOM(child))
        case other: String => throw new IllegalStateException(s"Unknown <metadata> child <${other}>")
      }
    meta
  }

  def parseLOM(el: Element): LOM = {
    def lom = newLOM
    for (child <- el.getChildElements)
      child.getTagName match {
        case "imsmd:general" => lom.setGeneral(parseGeneral(child))
      }
    lom
  }

  def parseGeneral(el: Element): General = {
    val general = newGeneral
    for (child <- el.getChildElements)
      child.getTagName match {
        case "imsmd:identifier" => general.setIdentifier(child.getTextContent)
        case "imsmd:title" => general.setTitle(parseLangString(child))
        case "imsmd:language" => general.setLanguage(child.getTextContent)
        case "imsmd:structure" => general.setStructure(parseStructure(child))
      }
    general
  }

  def parseStructure(el: Element): Structure = {
    val structure = newStructure
    for (child <- el.getChildElements)
      child.getTagName match {
        case "imsmd:source" => structure.setSource(parseLangString(child))
        case "imsmd:value" => structure.setValue(parseLangString(child))
      }
    structure
  }

  def parseLangString(el: Element): LangString = {
    val langString = newLangString
    langString.setLang(el.getAttribute("xml:lang"))
    langString.setText(el.getTextContent)
    langString
  }

}
