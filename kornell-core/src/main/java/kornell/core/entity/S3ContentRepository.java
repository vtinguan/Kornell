package kornell.core.entity;

public interface S3ContentRepository extends ContentRepository {
	public static String TYPE = EntityFactory.PREFIX + "contentRepo+json";
	
	String getUUID();
	void setUUID(String UUID);
	
	String getAccessKeyId();
	void setAccessKeyId(String accessKeyId);
	
	String getSecretAccessKey();
	void setSecretAccessKey(String secretAccessKey);
	
	String getBucketName();
	void setBucketName(String bucketName);
	
	String getPrefix();
	void setPrefix(String prefix);
	
	String getRegion();
	void setRegion(String region);
	
	String getInstitutionUUID();
	void setInstitutionUUID(String institutionUUID);
}
