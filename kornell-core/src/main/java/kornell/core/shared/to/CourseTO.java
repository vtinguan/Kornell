package kornell.core.shared.to;

import java.util.List;

import kornell.core.shared.data.Course;
import kornell.core.shared.data.Enrollment;



public interface CourseTO {
	public static final String TYPE = "application/vnd.kornell.v1.to.course+json;charset=UTF-8";
	
	Course getCourse();
	void setCourse(Course c);
	
	Enrollment getEnrollment();
	void setEnrollment(Enrollment e);
	
	/** An Actom is an undivisible units of learning activity, 
	 * such as a single question or slide **/
	List<String> getActoms();
	void setActoms(List<String> actoms);
	
	String getBaseURL();
	void setBaseURL(String baseURL);
}
