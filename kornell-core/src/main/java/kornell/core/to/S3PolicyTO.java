package kornell.core.to;


public interface S3PolicyTO {
	public static final String TYPE = TOFactory.PREFIX + "s3policy+json";
	
	String getAWSAccessKeyId();
	void setAWSAccessKeyId(String awsAccessKeyId);
	
	String getPolicy();
	void setPolicy(String policy);
	
	String getSignature();
	void setSignature(String signature);
	
	String getBucketName();
	void setBucketName(String bucketName);
	
	String getKey();
	void setKey(String key);
	
	String getSuccessActionRedirect();
	void setSuccessActionRedirect(String successActionRedirect);
}
