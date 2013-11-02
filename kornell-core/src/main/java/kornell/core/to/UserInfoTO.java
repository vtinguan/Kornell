package kornell.core.to;

import kornell.core.entity.Person;

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
}
