package kornell.core.entity;

import java.util.List;

public interface Registrations {
	public static String TYPE = EntityFactory.PREFIX + "registrations+json";
	List<Registration> getRegistrations();
	void setRegistrations(List<Registration> rs);
	
}
