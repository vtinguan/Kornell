package kornell.core.shared.event;

import java.util.Date;

public interface ActomEntered {
	public static final String TYPE = "application/vnd.kornell.v1.event.ActomEntered+json";

	Date getTime();
	void setTime(Date time);
	
	String getFromPersonUUID();
	void setFromPersonUUID(String fromPersonUUID);
	
	String getActomKey();
	void setActomKey(String actomKey);
}
