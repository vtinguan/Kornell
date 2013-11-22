package kornell.core.entity;

public interface CourseClass extends Named{
	
	public String getInstitutionUUID();
	public void setInstitutionUUID(String institutionUUID);

	public String getCouresVersionUUID();
	public String setCourseVersionUUID(String courseVersionUUID);
}
