package kornell.server.scorm.scorm12.cam

import com.google.web.bindery.autobean.vm.AutoBeanFactorySource
import java.util.List
import kornell.core.scorm.scorm12.cam.Organization
import java.lang.Boolean
import kornell.core.scorm.scorm12.cam.adlcp.PreRequisites
import kornell.core.scorm.scorm12.cam.CAM12Factory
import kornell.core.scorm.scorm12.cam.Metadata
import java.util.ArrayList
import kornell.core.scorm.scorm12.cam.Item
import kornell.core.scorm.scorm12.cam._

object CAM12 {
	val factory = AutoBeanFactorySource.create(classOf[CAM12Factory])
	
	def newManifest = factory.newManifest.as
	
	def newOrganizations(defaultOrganization:String=null,
	    organizationList:List[Organization]=new ArrayList()) = {
		val orgs = factory.newOrganizations.as
		orgs.setDefaultOrganization(defaultOrganization)
		orgs.setOrganizationList(organizationList)
		orgs
	}
	
	def newOrganization(identifier:String = null, 
	    title:String =null) = {
	  val org = factory.newOrganization.as
	  org.setIdentifier(identifier)
	  org.setTitle(title)
	  org
	}
	
	def newItem(identifier:String = null,
	    visible:String=null,
	    title:String=null,
	    items:List[Item]=new ArrayList()) = {
	  val item = factory.newItem.as
	  item.setIdentifier(identifier)
	  item.setTitle(title)
	  item.setItems(items)
	  if(visible != null) item.setVisible(Boolean.parseBoolean(visible))
	  item
	}
	
	def newPreRequisites(content:String = null,
	    theType:String = null):PreRequisites = {
	  val prereq = factory.newPreRequisites.as
	  prereq.setType(theType)
	  prereq.setContent(content)
	  prereq
	}
	
	def newMetadata():Metadata = {
	  val metadata = factory.newMetadata.as
	  metadata
	}
	
	def newResources(resourceList:List[Resource] = new ArrayList):Resources = {
	  val resources = factory.newResources.as
	  resources.setResourceList(resourceList)
	  resources
	}
	
	def newResource() = {
	  val res = factory.newResource.as
	  res.setFiles(new ArrayList)
	  res.setDependencies(new ArrayList)
	  res
	}
	
	def newFile() = {
	  val file = factory.newFile.as
	  file
	}

}