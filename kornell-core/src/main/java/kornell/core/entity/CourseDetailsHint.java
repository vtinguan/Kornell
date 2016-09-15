package kornell.core.entity;

public interface CourseDetailsHint extends Entity {
	public static String TYPE = EntityFactory.PREFIX + "courseDetailsHint+json";

	String getText();
	void setText(String text);
	
	CourseDetailsEntityType getEntityType();
	void setEntityType(CourseDetailsEntityType entityType);
	
	String getEntityUUID();
	void setEntityUUID(String entityUUID);
	
	Integer getIndex();
	void setIndex(Integer index);
	
	String getFontAwesomeClassName();
	void setFontAwesomeClassName(String fontAwesomeClassName);
}
