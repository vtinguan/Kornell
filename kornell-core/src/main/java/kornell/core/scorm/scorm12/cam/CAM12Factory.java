package kornell.core.scorm.scorm12.cam;

import kornell.core.scorm.scorm12.cam.adlcp.PreRequisites;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

public interface CAM12Factory extends AutoBeanFactory { 
	public static String PREFIX = "application/vnd.kornell.v1.scorm12.";
	/* IMS */
	AutoBean<Manifest> newManifest();
	AutoBean<Organizations> newOrganizations();
	AutoBean<Organization> newOrganization();
	AutoBean<Item> newItem();
	AutoBean<Metadata> newMetadata();
	AutoBean<Resources> newResources();
	AutoBean<Resource> newResource();
	AutoBean<File> newFile();
	/* ADLCP */
	AutoBean<PreRequisites> newPreRequisites();
}
