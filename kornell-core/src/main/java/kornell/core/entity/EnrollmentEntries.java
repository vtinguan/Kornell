package kornell.core.entity;

import java.util.Map;

public interface EnrollmentEntries {
	public static String TYPE = EntityFactory.PREFIX + "enrollmententries+json"; 
	
	String getEnrollmentUUID();
	void setEnrollmentUUID(String enrollmentUUID);
	
	Map</*enrollmentUUID*/String,Map<String /*actomKey*/,ActomEntries>> getModuleEntries();
	void setModuleEntries(Map</*enrollmentUUID*/String,Map<String /*actomKey*/,ActomEntries>> moduleEntries);
}
