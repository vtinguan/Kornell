package kornell.core.event;

import kornell.core.value.Date;



public interface Event {

	String getUUID();
	void setUUID(String uuid);
	
	//TODO: Consider timezone issues with i18n
	String getEventFiredAt();
	void setEventFiredAt(String time);
}
