package kornell.core.event;




public interface Event {

	String getUUID();
	void setUUID(String uuid);
	
	//TODO: Consider timezone issues with i18n
	String getEventFiredAt();
	void setEventFiredAt(String eventFiredAt);
}
