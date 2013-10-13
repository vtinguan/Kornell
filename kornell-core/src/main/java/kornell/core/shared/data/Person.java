package kornell.core.shared.data;

import java.util.Date;

public interface Person {
	
	String getUUID();
	void setUUID(String uuid);
	String getFullName();
	void setFullName(String fullName);
	String getLastPlaceVisited();
	void setLastPlaceVisited(String lastPlaceVisited);
	String getEmail();
	void setEmail(String email);
	String getFirstName();
	void setFirstName(String firstName);
	String getLastName();
	void setLastName(String lastName);
	String getCompany();
	void setCompany(String company);
	String getTitle();
	void setTitle(String title);
	String getSex();
	void setSex(String sex);
	Date getBirthDate();
	void setBirthDate(Date birthDate);
	
}
