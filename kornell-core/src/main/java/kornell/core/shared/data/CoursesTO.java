package kornell.core.shared.data;

import java.util.List;

public interface CoursesTO {
	public static final String MIME_TYPE = "application/vnd.kornell.v1.to.courses+json;charset=utf-8";
	
	List<CourseTO> getCourses(); 
	void setCourses(List<CourseTO> courses);
}
