package kornell.core.shared.to;

import kornell.core.shared.data.Person;

public interface UserInfoTO {
	public static String TYPE = "application/vnd.kornell.v1.to.userinfo+json";
	 
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
