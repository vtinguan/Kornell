package kornell.core.shared.data;

public interface Principal {
	String getUUID();
	void setUUID(String uuid);
	String getUsername();
	void setUsername(String username);
	String getPersonUUID();	
	void setPersonUUID(String personUUID);
}
