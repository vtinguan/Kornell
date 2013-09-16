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
	
	/** Undivisible Units of Learning Activity, such as one question or one slide **/
	List<String> getActomsURLs();
	void setActormsURLs(List<String> actomsURLs);
	
}
