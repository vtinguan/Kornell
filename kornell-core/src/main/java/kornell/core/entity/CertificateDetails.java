package kornell.core.entity;

public interface CertificateDetails extends Entity {
	public static String TYPE = EntityFactory.PREFIX + "certificateDetails+json";

	String getBgImage();
	void setBgImage(String bgImage);
	
	CourseDetailsEntityType getEntityType();
	void setEntityType(CourseDetailsEntityType entityType);
	
	String getEntityUUID();
	void setEntityUUID(String entityUUID);
}
