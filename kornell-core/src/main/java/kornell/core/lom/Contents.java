package kornell.core.lom;

import kornell.core.to.CourseTO;

public interface Contents extends Node{
	public static String TYPE = LOMFactory.PREFIX+ "contents+json";
	
	CourseTO getCourseTO();
	void setCourseTO(CourseTO to);
}
