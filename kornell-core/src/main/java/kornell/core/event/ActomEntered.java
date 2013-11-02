package kornell.core.event;

import java.util.Date;

public interface ActomEntered {
	public static final String TYPE = EventFactory.PREFIX+"ActomEntered+json";

	Date getTime();
	void setTime(Date time);
	
	String getFromPersonUUID();
	void setFromPersonUUID(String fromPersonUUID);
	
	String getActomKey();
	void setActomKey(String actomKey);
}
