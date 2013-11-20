package kornell.core.entity;

public interface Institution extends Named {
	public static String TYPE = EntityFactory.PREFIX + "institution+json";

	String getTerms();
	void setTerms(String terms);
	
	String getAssetsURL();
	void setAssetsURL(String assetsURL);
	
	String getBaseURL();
	void setBaseURL(String baseURL);
}
