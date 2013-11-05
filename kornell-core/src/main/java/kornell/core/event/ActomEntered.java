package kornell.core.event;

import java.util.Date;

public interface ActomEntered {
	public static final String TYPE = EventFactory.PREFIX+"ActomEntered+json";

	String getUUID();
	void setUUID(String uuid);
	
	//TODO: Consider timezone issues with i18n
	Date getEventFiredAt();
	void setEventFiredAt(Date time);
	
	String getFromPersonUUID();
	void setFromPersonUUID(String fromPersonUUID);
	
	String getCourseUUID();
	
	String getActomKey();
	void setActomKey(String actomKey);
}
