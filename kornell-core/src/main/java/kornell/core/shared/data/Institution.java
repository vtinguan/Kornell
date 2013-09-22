package kornell.core.shared.data;

public interface Institution extends Named {
	public static String TYPE = "application/vnd.kornell.v1.institution+json";

	String getTerms();
	void setTerms(String terms);
	
	String getAssetsURL();
	void setAssetsURL(String assetsURL);
	
}
