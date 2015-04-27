package kornell.core.to;

import java.util.List;

import kornell.core.entity.CourseVersion;

public interface CourseVersionsTO extends Page {
	public static final String TYPE = TOFactory.PREFIX + "courseVersions+json";
	
	List<CourseVersion> getCourseVersions(); 
	void setCourseVersions(List<CourseVersion> courseVersion);

}
