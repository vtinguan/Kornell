package kornell.core.to;

import java.util.Map;

import kornell.core.entity.Institution;
import kornell.core.entity.Registration;

public interface RegistrationsTO {
	public static final String TYPE = TOFactory.PREFIX + "registrations+json";
	
	Map<Registration, Institution> getRegistrationsWithInstitutions();
	void setRegistrationsWithInstitutions(Map<Registration, Institution> registrationsWithInstitutions);
	
}
