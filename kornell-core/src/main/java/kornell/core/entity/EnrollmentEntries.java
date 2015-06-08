package kornell.core.entity;

import java.util.Map;

public interface EnrollmentEntries {
	public static String TYPE = EntityFactory.PREFIX + "enrollmententries+json"; 
	
	Map</*ActomKey*/String,ActomEntries> getActomEntriesMap();
	void setActomEntriesMap(Map</*ActomKey*/String,ActomEntries> actomEntriesMap);
}
