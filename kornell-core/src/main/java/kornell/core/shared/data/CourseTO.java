package kornell.core.shared.data;



public interface CourseTO {
	public static final String MIME_TYPE = "application/vnd.kornell.v1.to.course+json;charset=UTF-8";
	
	Course getCourse();
	void setCourse(Course c);
	Enrollment getEnrollment();
	void setEnrollment(Enrollment e);
}
