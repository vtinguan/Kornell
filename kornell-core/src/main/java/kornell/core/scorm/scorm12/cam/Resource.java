package kornell.core.scorm.scorm12.cam;

import java.util.List;

public interface Resource {
	String getIdentifier();
	void setIdentifier(String identifier);
	
	String getType();
	void setType(String type);
	
	String getScormType();
	void setScormType(String ScormType);
	
	String getHref();
	void setHref(String href);

	Metadata getMetadata();
	void setMetadata(Metadata metadata);
	
	List<File> getFiles();
	void setFiles(List<File> files);
	
	List<String> getDependencies();
	void setDependencies(List<String> dependencies);
	
	String getBase();
	void setBase(String base);
}
