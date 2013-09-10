package kornell.core.shared.to;

import java.util.Map;

import kornell.core.shared.data.Institution;
import kornell.core.shared.data.Registration;

public interface RegistrationsTO {
	public static final String TYPE = "application/vnd.kornell.v1.to.registrations+json";
	
	Map<Registration, Institution> getRegistrationsWithInstitutions();
	void setRegistrationsWithInstitutions(Map<Registration, Institution> registrationsWithInstitutions);
	
}
