package kornell.core.scorm.scorm12.cam;

import kornell.core.scorm.scorm12.cam.imsmd.LOM;

public interface Metadata {
	/* IMS */
	String getSchema();
	void setSchema(String schema);
	String getSchemaVersion();
	void setSchemaVersion(String schemaVersion);
	
	/* ADLCP */
	String getLocation();
	void setLocation(String location);
	
	/*IMSMD*/
	LOM getLOM();
	void setLOM(LOM lom);
}
