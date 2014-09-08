package kornell.server.scorm.scorm12

import kornell.server.content.ContentManager
import kornell.core.to.ActionTO
import kornell.server.repository.TOs
import scala.collection.JavaConverters._
import kornell.core.to.ActionType
import kornell.server.scorm.scorm12.cam._
import kornell.core.scorm.scorm12.cam._
import kornell.core.util.StringUtils._

class SCORM12PackageManager(cm:ContentManager) {
  //TODO: Public Interface
  def launch():ActionTO = {
	  val manifest = getManifest
	  val orgs = manifest.getOrganizations
	  val defaultOrg = orgs.getDefaultOrganization
	  val orgList = orgs.getOrganizationList asScala
	  val org = orgList.find { o => o.getIdentifier == defaultOrg}
	  val resources = manifest.getResources
	  val action = org match {
	    case Some(org) => launch(org,resources)
	    case None => ???
	  }
	  action
  }
  
  //TODO: Private Impl
  
  def launch(org:Organization,resources:Resources): ActionTO = {
	  val res = firstResource(org.getItems asScala,resources)
	  res match {
	    case Some(res) => launch(res)
	    case None => ???
	  }	  
  }
  
  def launch(res:Resource) = {
      val path = cm.getPath(res.getHref)
	  val action = TOs.newActionTO
	  action.setType(ActionType.OpenURL)
	  action.setProperties(Map("href" -> path) asJava)
	  action
  }
  
  def firstResource(items:Seq[Item],resources:Resources):Option[Resource] =  
    items.filter{ it => isSome(it.getIdentifierRef) }
    	.headOption
    	.flatMap { it => findResource(it.getIdentifierRef,resources) }
    	

  
  def findResource(id:String,resources:Resources) = 
    resources.getResourceList.asScala.find{ res => id == res.getIdentifier}
  
  
  //TODO: Cache
  def getManifest:Manifest = CAM12DOMParser.parse(cm.getObjectStream("imsmanifest.xml"))
  

}

object SCORM12PackageManager{
  def apply(cm:ContentManager) = new SCORM12PackageManager(cm)
}