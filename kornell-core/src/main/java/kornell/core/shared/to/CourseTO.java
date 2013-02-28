package kornell.core.shared.to;

import java.math.BigDecimal;


public interface CourseTO {
	public static final String MIME_TYPE = "application/vnd.kornell.v1.to.course+json; charset=UTF-8";
	
	String getCourseUUID();
	void setCourseUUID(String courseUUID);
	String getDescription();
	void setDescription(String description);
	String getTitle();
	void setTitle(String title);
	String getThumbDataURI();
	void setThumbDataURI(String thumbDataURI); 
	BigDecimal getProgress();
	void setProgress(BigDecimal progress);
}
