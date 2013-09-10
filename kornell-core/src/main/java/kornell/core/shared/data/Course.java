package kornell.core.shared.data;

public interface Course extends Entity{

	String getCode();
	void setCode(String code);
	
	String getDescription();
	void setDescription(String description);
	
	String getTitle();
	void setTitle(String title);
	
	String getAssetsURL();
	void setAssetsURL(String assetsURL);
	
	String getInfoJson();
	void setInfoJson(String infoJson);

	//TODO: Delete
	@Deprecated
	String getThumbDataURI();
	@Deprecated
	void setThumbDataURI(String thumbDataURI); 
}
