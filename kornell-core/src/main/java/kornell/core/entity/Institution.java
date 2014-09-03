package kornell.core.entity;

import java.util.Date;


public interface Institution extends Named {
	public static String TYPE = EntityFactory.PREFIX + "institution+json";

	String getFullName();
	void setFullName(String fullName);

	String getTerms();
	void setTerms(String terms);
	
	String getAssetsURL();
	void setAssetsURL(String assetsURL);
	
	String getBaseURL();
	void setBaseURL(String baseURL);
	
	boolean isDemandsPersonContactDetails();
	void setDemandsPersonContactDetails(boolean demandsPersonContactDetails);

	boolean isAllowRegistration();
	void setAllowRegistration(boolean allowRegistration);
	
	Date getActivatedAt();
	void setActivatedAt(Date activatedAt);

	String getSkin();
	void setSkin(String skin);
}
