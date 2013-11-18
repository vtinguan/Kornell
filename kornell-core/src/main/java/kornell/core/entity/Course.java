package kornell.core.entity;

public interface Course extends Entity{

	String getCode();
	void setCode(String code);
	
	String getDescription();
	void setDescription(String description);
	
	String getTitle();
	void setTitle(String title);
	
	String getInfoJson();
	void setInfoJson(String infoJson);
	
	String getRepositoryUUID();
	void setRepositoryUUID(String repositoryUUID);

	//TODO: Delete
	@Deprecated
	String getThumbDataURI();
	@Deprecated
	void setThumbDataURI(String thumbDataURI); 
}
