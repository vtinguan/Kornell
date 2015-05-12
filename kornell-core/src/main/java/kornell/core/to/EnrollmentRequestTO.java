package kornell.core.to;

import kornell.core.entity.RegistrationType;

public interface EnrollmentRequestTO {
	public static final String TYPE = TOFactory.PREFIX + "enrollmentRequest+json";
	
	String getInstitutionUUID();
	void setInstitutionUUID(String institutionUUID);
	String getCourseClassUUID();
	void setCourseClassUUID(String courseClassUUID);
	String getCourseVersionUUID();
	void setCourseVersionUUID(String courseVersionUUID);
	String getFullName();
	void setFullName(String fullName);
	String getUsername();
	void setUsername(String username);
	String getPassword();
	void setPassword(String password);
	RegistrationType getRegistrationType();
	void setRegistrationType(RegistrationType registrationType);
	String getInstitutionRegistrationPrefixUUID();
	void setInstitutionRegistrationPrefixUUID(String institutionRegistrationPrefixUUID);
	Boolean isCancelEnrollment();
	void setCancelEnrollment(Boolean cancelEnrollment);
}
