package kornell.core.entity;

import java.math.BigDecimal;

public interface CourseClass extends Named{
	public static String TYPE = EntityFactory.PREFIX + "courseClass+json";
	
	String getInstitutionUUID();
	void setInstitutionUUID(String institutionUUID);

	String getCourseVersionUUID();
	void setCourseVersionUUID(String courseVersionUUID);
	
	BigDecimal getRequiredScore();
	void setRequiredScore(BigDecimal requiredScore);
	
	Boolean isPublicClass();
	void setPublicClass(Boolean publicClass);
}
