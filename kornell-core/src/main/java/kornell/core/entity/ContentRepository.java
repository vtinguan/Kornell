package kornell.core.entity;

public interface ContentRepository {
	String getUUID();
	void setUUID(String UUID);
	
	String getRepositoryType();
	void setRepostoryType(String repositoryType);
}
