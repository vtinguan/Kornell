package kornell.core.to;


public interface EnrollmentRequestTO {
	public static final String TYPE = TOFactory.PREFIX + "enrollmentRequest+json";
	
	String getInstitutionUUID();
	void setInstitutionUUID(String institutionUUID);
	String getCourseClassUUID();
	void setCourseClassUUID(String courseClassUUID);
	String getFullName();
	void setFullName(String fullName);
	String getEmail();
	void setEmail(String email);
}
