package kornell.core.entity;

import java.math.BigDecimal;
import java.util.Date;

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
	
	Boolean isEnrollWithCPF();
	void setEnrollWithCPF(Boolean enrollWithCPF);
	
	Integer getMaxEnrollments();
	void setMaxEnrollments(Integer maxEnrollments);
	
	Date getCreatedAt();
	void setCreatedAt(Date createdAt);
	
	String getCreatedBy();
	void setCreatedBy(String createdBy);
	
	CourseClassState getState();
	void setState(CourseClassState state);
}
