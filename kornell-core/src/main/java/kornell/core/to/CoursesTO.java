package kornell.core.to;

import java.util.List;

import kornell.core.entity.Course;

public interface CoursesTO extends Page {
	public static final String TYPE = TOFactory.PREFIX + "courses+json";
	
	List<Course> getCourses(); 
	void setCourses(List<Course> courses);

}
