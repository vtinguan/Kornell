package kornell.core.event;

import java.util.Date;

public interface Event {

	String getUUID();
	void setUUID(String uuid);
	
	//TODO: Consider timezone issues with i18n
	Date getEventFiredAt();
	void setEventFiredAt(Date time);
}
