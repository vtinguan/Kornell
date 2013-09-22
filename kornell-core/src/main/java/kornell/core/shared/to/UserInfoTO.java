package kornell.core.shared.to;

import kornell.core.shared.data.Person;

public interface UserInfoTO {
	public static String TYPE = "application/vnd.kornell.v1.to.userinfo+json";
	 
	Person getPerson();
	void setPerson(Person p);
	
	boolean isSigningNeeded();
	void setSigningNeeded(boolean siginingNeeded);
	
	String getLastPlaceVisited();
	void setLastPlaceVisited(String lastPlaceVisited);
	
	String getInstitutionAssetsURL();
	void setInstitutionAssetsURL(String InstitutionAssetsURL);
}
