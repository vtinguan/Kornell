package kornell.core.to;

import kornell.core.entity.CourseClass;
import kornell.core.entity.Enrollment;

public interface CourseClassTO {
	public static final String TYPE = TOFactory.PREFIX+"courseclass+json";
	
	CourseClass getCourseClass();
	void setCourseClass(CourseClass c);
	
	CourseVersionTO getCourseVersionTO();
	void setCourseVersionTO(CourseVersionTO t);
	
	Enrollment getEnrollment();
	void setEnrollment(Enrollment e);
	
}
