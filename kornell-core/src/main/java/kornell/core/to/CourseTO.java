package kornell.core.to;

import kornell.core.entity.Course;
import kornell.core.entity.Enrollment;

public interface CourseTO {
	public static final String TYPE = TOFactory.PREFIX+"course+json";
	
	Course getCourse();
	void setCourse(Course c);
	
	Enrollment getEnrollment();
	void setEnrollment(Enrollment e);
	
	String getDistributionURL();
	void setBaseURL(String getDistributionURL);
}
