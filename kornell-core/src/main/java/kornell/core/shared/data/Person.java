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
	Boolean isUsernamePrivate();
	void setUsernamePrivate(Boolean usernamePrivate);
	Boolean isEmailPrivate();
	void setEmailPrivate(Boolean emailPrivate);
	Boolean isFirstNamePrivate();
	void setFirstNamePrivate(Boolean firstNamePrivate);
	Boolean isLastNamePrivate();
	void setLastNamePrivate(Boolean lastNamePrivate);
	Boolean isCompanyPrivate();
	void setCompanyPrivate(Boolean companyPrivate);
	Boolean isTitlePrivate();
	void setTitlePrivate(Boolean titlePrivate);
	Boolean isSexPrivate();
	void setSexPrivate(Boolean sexPrivate);
	Boolean isBirthDatePrivate();
	void setBirthDatePrivate(Boolean birthDatePrivate);
	
}
