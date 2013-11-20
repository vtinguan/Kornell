package kornell.core.event;


public interface ActomEntered extends Event {
	public static final String TYPE = EventFactory.PREFIX+"ActomEntered+json";
	
	String getFromPersonUUID();
	void setFromPersonUUID(String fromPersonUUID);
	
	String getCourseUUID();
	void setCourseUUID(String courseUUID);
	
	String getActomKey();
	void setActomKey(String actomKey);
}
