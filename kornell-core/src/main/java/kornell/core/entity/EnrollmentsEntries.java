package kornell.core.entity;

import java.util.Map;

public interface EnrollmentsEntries {
	public static String TYPE = EntityFactory.PREFIX + "enrollmentsentries+json"; 
	
	String getEnrollmentUUID();
	void setEnrollmentUUID(String enrollmentUUID);
	
	Map</*enrollmentUUID*/ String,EnrollmentEntries> getEnrollmentEntriesMap();
	void setEnrollmentEntriesMap(Map</*enrollmentUUID*/ String,EnrollmentEntries> enrollmentsEntries);
}
