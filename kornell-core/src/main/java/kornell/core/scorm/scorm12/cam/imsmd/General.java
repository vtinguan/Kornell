package kornell.core.scorm.scorm12.cam.imsmd;

public interface General {
	String getIdentifier();
	void setIdentifier(String identifier);
	
	LangString getTitle();
	void setTitle(LangString title);
	
	String getLanguage();
	void setLanguage(String language);
	
	Structure getStructure();
	void setStructure(Structure structure);
	
	
}
