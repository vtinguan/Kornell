package kornell.core.to;

public interface CourseDetailsTO {
	public static final String TYPE = TOFactory.PREFIX + "details+json"; 

	String getCourseName();
	void setCourseName(String courseName);
	
	String getCourseClassName();
	void setCourseClassName(String courseClassName);
	
	InfosTO getInfosTO();
	void setInfosTO(InfosTO infosTO);
}
