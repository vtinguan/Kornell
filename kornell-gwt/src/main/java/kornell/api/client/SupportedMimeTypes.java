package kornell.api.client;

import java.util.HashMap;

import kornell.core.shared.to.CourseTO;
import kornell.core.shared.to.CoursesTO;

public class SupportedMimeTypes extends HashMap<String,Class>{
	
	public SupportedMimeTypes() {
		put(CoursesTO.MIME_TYPE.toLowerCase(), CoursesTO.class);
		put(CourseTO.MIME_TYPE.toLowerCase(), CourseTO.class);		
	}
	 
}
