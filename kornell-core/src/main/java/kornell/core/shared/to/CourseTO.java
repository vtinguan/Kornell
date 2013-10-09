package kornell.core.shared.to;

import kornell.core.shared.data.Course;
import kornell.core.shared.data.Enrollment;

public interface CourseTO {
	public static final String TYPE = "application/vnd.kornell.v1.to.course+json;charset=UTF-8";
	
	Course getCourse();
	void setCourse(Course c);
	
	Enrollment getEnrollment();
	void setEnrollment(Enrollment e);
	
	String getBaseURL();
	void setBaseURL(String baseURL);
}
