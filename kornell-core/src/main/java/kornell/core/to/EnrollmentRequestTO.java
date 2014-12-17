package kornell.core.to;

import kornell.core.entity.RegistrationEnrollmentType;


public interface EnrollmentRequestTO {
	public static final String TYPE = TOFactory.PREFIX + "enrollmentRequest+json";
	
	String getInstitutionUUID();
	void setInstitutionUUID(String institutionUUID);
	String getCourseClassUUID();
	void setCourseClassUUID(String courseClassUUID);
	String getFullName();
	void setFullName(String fullName);
	String getUsername();
	void setUsername(String username);
	String getPassword();
	void setPassword(String password);
	RegistrationEnrollmentType getRegistrationEnrollmentType();
	void setRegistrationEnrollmentType(RegistrationEnrollmentType registrationEnrollmentType);
	Boolean isCancelEnrollment();
	void setCancelEnrollment(Boolean cancelEnrollment);
}
