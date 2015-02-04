package kornell.core.event;


public interface EnrollmentTransfered extends Event {
	public static final String TYPE = EventFactory.PREFIX+"EnrollmentTransfered+json";
	
	String getFromPersonUUID();
	void setFromPersonUUID(String fromPersonUUID);
	
	String getEnrollmentUUID();
	void setEnrollmentUUID(String enrollmentUUID);
	
	String getFromCourseClassUUID();
	void setFromCourseClassUUID(String courseClassUUID);
	
	String getToCourseClassUUID();
	void setToCourseClassUUID(String courseClassUUID);
}
