package kornell.core.to;

import kornell.core.entity.Course;
import kornell.core.entity.CourseVersion;

public interface CourseVersionTO {
	public static final String TYPE = TOFactory.PREFIX+"courseVersion+json";
	
	CourseVersion getCourseVersion();
	void setCourseVersion(CourseVersion c);
	
	Course getCourse();
	void setCourse(Course c);
	
	String getDistributionURL();
	void setDistributionURL(String getDistributionURL);
}
