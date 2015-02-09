package kornell.core.to;

import java.util.Set;

import kornell.core.entity.Enrollments;
import kornell.core.entity.InstitutionRegistrationPrefix;
import kornell.core.entity.Person;
import kornell.core.entity.Role;

public interface UserInfoTO {
	public static String TYPE = TOFactory.PREFIX + "userinfo+json";
	 
	Person getPerson();
	void setPerson(Person p);
	
	InstitutionRegistrationPrefix getInstitutionRegistrationPrefix();
	void setInstitutionRegistrationPrefix(InstitutionRegistrationPrefix institutionRegistrationPrefix);
	
	String getUsername();
	void setUsername(String username);
	
	String getLastPlaceVisited();
	void setLastPlaceVisited(String lastPlaceVisited);
	
	Set<Role> getRoles();
	void setRoles(Set<Role> roles);
	
	Enrollments getEnrollments();
	void setEnrollments(Enrollments e);
}
