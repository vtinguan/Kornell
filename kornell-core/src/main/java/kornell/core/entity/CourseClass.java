package kornell.core.entity;

public interface CourseClass extends Named{
	
	String getInstitutionUUID();
	void setInstitutionUUID(String institutionUUID);

	String getCourseVersionUUID();
	void setCourseVersionUUID(String courseVersionUUID);
}
