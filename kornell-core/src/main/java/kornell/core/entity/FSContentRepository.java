package kornell.core.entity;

public interface FSContentRepository extends ContentRepository {
	String getPath();
	void setPath(String path);
	
	String getPrefix();
	void setPrefix(String prefix);
}
