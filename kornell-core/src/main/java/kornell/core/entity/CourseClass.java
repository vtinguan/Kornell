package kornell.core.entity;

import java.math.BigDecimal;

public interface CourseClass extends Named{
	
	String getInstitutionUUID();
	void setInstitutionUUID(String institutionUUID);

	String getCourseVersionUUID();
	void setCourseVersionUUID(String courseVersionUUID);
	
	BigDecimal getRequiredScore();
	void setRequiredScore(BigDecimal requiredScore);
}
