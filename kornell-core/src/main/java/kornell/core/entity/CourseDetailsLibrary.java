package kornell.core.entity;

import java.util.Date;

public interface CourseDetailsLibrary extends Entity {
	public static String TYPE = EntityFactory.PREFIX + "courseDetailsLibrary+json";
	
	String getTitle();
	void setTitle(String title);

	String getDescription();
	void setDescription(String description);
	
	CourseDetailsEntityType getEntityType();
	void setEntityType(CourseDetailsEntityType entityType);
	
	String getEntityUUID();
	void setEntityUUID(String entityUUID);
	
	Integer getIndex();
	void setIndex(Integer index);
	
	Integer getSize();
	void setSize(Integer size);
	
	String getPath();
	void setPath(String path);
	
	Date getUploadDate();
	void setUploadDate(Date uploadDate);
	
	String getFontAwesomeClassName();
	void setFontAwesomeClassName(String fontAwesomeClassName);
}
