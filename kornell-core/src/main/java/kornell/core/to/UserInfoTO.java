package kornell.core.to;

import java.util.List;
import java.util.Set;

import kornell.core.entity.Person;
import kornell.core.entity.Registration;
import kornell.core.entity.Role;
import kornell.core.entity.RoleType;

public interface UserInfoTO {
	public static String TYPE = TOFactory.PREFIX + "userinfo+json";
	 
	Person getPerson();
	void setPerson(Person p);
	
	String getUsername();
	void setUsername(String username);
	
	String getEmail();
	void setEmail(String email);
	
	boolean isSigningNeeded();
	void setSigningNeeded(boolean siginingNeeded);
	
	String getLastPlaceVisited();
	void setLastPlaceVisited(String lastPlaceVisited);
	
	String getInstitutionAssetsURL();
	void setInstitutionAssetsURL(String InstitutionAssetsURL);
	
	Set<Role> getRoles();
	void setRoles(Set<Role> roles);
	
	RegistrationsTO getRegistrationsTO();
	void setRegistrationsTO(RegistrationsTO r);
}
