package kornell.core.to;

public interface RegistrationRequestTO {
	public static final String TYPE = TOFactory.PREFIX + "registrationRequest+json";
	
	String getInstitutionUUID();
	void setInstitutionUUID(String institutionUUID);
	String getFullName();
	void setFullName(String fullName);
	String getEmail();
	void setEmail(String email);
	String getPassword();
	void setPassword(String password);
}
