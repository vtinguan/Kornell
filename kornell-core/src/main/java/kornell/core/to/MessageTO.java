package kornell.core.to;

import kornell.core.entity.Person;
import kornell.core.entity.Role;

public interface MessageTO {
	public static String TYPE = TOFactory.PREFIX + "role+json";
	
	Role getRole();
	void setRole(Role role);
	
	Person getPerson();
	void setPerson(Person person);
}
