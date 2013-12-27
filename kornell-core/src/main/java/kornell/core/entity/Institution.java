package kornell.core.entity;

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
}
