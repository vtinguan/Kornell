package kornell.server.scorm.scorm12

import scala.language.postfixOps
import scala.language.implicitConversions
import kornell.server.content.ContentManager
import kornell.core.to.ActionTO
import kornell.server.repository.TOs
import scala.collection.JavaConverters._
import kornell.core.to.ActionType
import kornell.server.scorm.scorm12.cam._
import kornell.core.scorm.scorm12.cam._
import kornell.core.util.StringUtils._
import kornell.core.entity.Enrollment
import kornell.server.jdbc.repository.ActomEntriesRepo
import kornell.server.scorm.scorm12.rte.RTE12
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import java.util.logging.Logger

class SCORM12PackageManager(cm: ContentManager) {
   val log = Logger.getLogger(classOf[SCORM12PackageManager].getName)
   log.info("Instantiating SCORM12PackageManager")
   
  
  //TODO: Public Interface    
  def launch(e: Enrollment): ActionTO = {
    //TODO: Recover from last
    val itemOpt = actoms.headOption //first item
    val itemResource = itemOpt.flatMap { item =>
      resourceList.find { res => 
        res.getIdentifier == item.getIdentifierRef
      }.map { res =>
        (item, res)
      }
    }

    val action = itemResource match {
      case Some((item, res)) => launch(e, item, res)
      case None => ???
    }

    action
  }
  
  implicit class ItemOps(item:Item) {
    def flatten:Seq[Item] = item +: item.getItems.asScala.flatMap(si => si.flatten)
    def isResource = isSome(item.getIdentifierRef)
  }

  //TODO: Private Impl
  val manifest = getManifest
  val orgs = manifest.getOrganizations
  val defaultOrgId = orgs.getDefaultOrganization
  val orgList = orgs.getOrganizationList asScala
  //TODO: Support multiple organizations
  val defaultOrg = orgList.find { o => o.getIdentifier == defaultOrgId }
  val items = defaultOrg.map { _.getItems.asScala.flatMap{_.flatten}.filter{_.isResource} }.get
  val actoms = items.filter { it => isSome(it.getIdentifierRef) }
  val resources = manifest.getResources
  val resourceList = resources.getResourceList asScala

  def launch(e: Enrollment, item: Item, res: Resource) = {
    val path = cm.getURL(res.getHref)
    val resId = res.getIdentifier
    val data = ActomEntriesRepo.getValues(e.getUUID, item.getIdentifier, item.getIdentifierRef)
    RTE12.newOpenSCO12Action(resId, path, data)
  }

  def findResource(id: String, resources: Resources) =
    resources.getResourceList.asScala.find { res => id == res.getIdentifier }

  def getManifest: Manifest = CAM12DOMParser.parse(cm.getObjectStream("imsmanifest.xml"))
}