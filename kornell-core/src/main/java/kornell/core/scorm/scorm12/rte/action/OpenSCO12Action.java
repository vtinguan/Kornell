package kornell.core.scorm.scorm12.rte.action;

import java.util.Map;

import kornell.core.scorm.scorm12.rte.RTEManifest;

public interface OpenSCO12Action {
	String getResourceId();
	void setResourceId(String resourceId);
	
	String getHref();
	void setHref(String href);
	
	Map<String, String> getData();
	void setData(Map<String,String> properties);
	
	RTEManifest getRTEManifest();
	void setRTEManifest(RTEManifest manifest);
}
