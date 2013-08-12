package kornell.core.shared.data;

import java.util.List;

public interface Registrations {
	public static String TYPE = "application/vnd.kornell.v1.registrations+json";

	List<Registration> getRegistrations();
	void setRegistrations(List<Registration> rs);
	
}
