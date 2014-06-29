package kornell.core.event;

import kornell.core.entity.CourseClassState;


public interface CourseClassStateChanged extends Event {
	public static final String TYPE = EventFactory.PREFIX+"CourseClassStateChanged+json";
	
	String getFromPersonUUID();
	void setFromPersonUUID(String fromPersonUUID);
	
	String getCourseClassUUID();
	void setCourseClassUUID(String courseClassUUID);
	
	CourseClassState getFromState();
	void setFromState(CourseClassState fromState);
	
	CourseClassState getToState();
	void setToState(CourseClassState toState);
}
