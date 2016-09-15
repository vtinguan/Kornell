package kornell.core.entity;

public interface CourseDetailsSection extends Entity {
	public static String TYPE = EntityFactory.PREFIX + "courseDetailsSection+json";
	
	String getTitle();
	void setTitle(String title);

	String getText();
	void setText(String text);
	
	CourseDetailsEntityType getEntityType();
	void setEntityType(CourseDetailsEntityType entityType);
	
	String getEntityUUID();
	void setEntityUUID(String entityUUID);
	
	Integer getIndex();
	void setIndex(Integer index);
}
