package kornell.core.shared.data;

public interface Course {
	String getUUID();
	void setUUID(String UUID);
	
	String getCode();
	void setCode(String code);
	
	String getDescription();
	void setDescription(String description);
	
	String getTitle();
	void setTitle(String title);
	
	String getAssetsURL();
	void setAssetsURL(String assetsURL);

	//TODO: Delete
	@Deprecated
	String getThumbDataURI();
	@Deprecated
	void setThumbDataURI(String thumbDataURI); 
}
