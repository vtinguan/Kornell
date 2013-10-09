package kornell.core.shared.data;

import kornell.core.shared.to.CourseTO;

public interface Contents extends Node{
	public static String TYPE = "application/vnd.kornell.v1.contents+json";
	
	CourseTO getCourseTO();
	void setCourseTO(CourseTO to);
}
