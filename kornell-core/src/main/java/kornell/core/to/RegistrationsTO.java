package kornell.core.to;

import java.util.List;

import kornell.core.entity.Registration;

public interface RegistrationsTO {
	public static final String TYPE = TOFactory.PREFIX + "registrations+json"; 
	
	List<Registration> getRegistrations();
	void setRegistrations(List<Registration> registrations);
	
}
