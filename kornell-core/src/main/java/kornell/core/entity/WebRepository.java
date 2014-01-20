package kornell.core.entity;

public interface WebRepository {
	public static String TYPE = EntityFactory.PREFIX + "webrepository+json";

	String getUUID();
	void setUUID(String uuid);
	
	String getDistributionURL();
	void setDistributionURL(String distributionURL);
	
	String getPrefix();
	void setPrefix(String distributionURL);
}
