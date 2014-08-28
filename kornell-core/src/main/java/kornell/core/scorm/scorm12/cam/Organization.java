package kornell.core.scorm.scorm12.cam;


public interface Organization extends HasItems, HasMetadata{
	String getIdentifier();
	void setIdentifier(String getIdentifier);
	
	String getTitle();
	void setTitle(String title);
}
