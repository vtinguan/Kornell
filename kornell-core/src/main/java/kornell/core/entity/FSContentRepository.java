package kornell.core.entity;

public interface FSContentRepository extends ContentRepository {
	public static String TYPE = EntityFactory.PREFIX + "fsContentRepo+json";
	
	String getPath();
	void setPath(String path);
	
	String getPrefix();
	void setPrefix(String prefix);
	
	String getInstitutionUUID();
	void setInstitutionUUID(String institutionUUID);
}
