package kornell.core.to;

import java.util.Set;

import kornell.core.entity.Person;
import kornell.core.entity.Role;

public interface UserInfoTO {
	public static String TYPE = TOFactory.PREFIX + "userinfo+json";
	 
	Person getPerson();
	void setPerson(Person p);
	
	String getUsername();
	void setUsername(String username);
	
	String getLastPlaceVisited();
	void setLastPlaceVisited(String lastPlaceVisited);
	
	Set<Role> getRoles();
	void setRoles(Set<Role> roles);
	
	RegistrationsTO getRegistrationsTO();
	void setRegistrationsTO(RegistrationsTO r);
	
	EnrollmentsTO getEnrollmentsTO();
	void setEnrollmentsTO(EnrollmentsTO e);
}
