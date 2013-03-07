package kornell.api.client;

import java.util.HashMap;

import kornell.core.shared.data.CourseTO;
import kornell.core.shared.data.CoursesTO;

public class SupportedMimeTypes extends HashMap<String,Class<?>>{
	private static final long serialVersionUID = 8495421711509512346L;

	public SupportedMimeTypes() {
		put(CoursesTO.MIME_TYPE.toLowerCase(), CoursesTO.class);
		put(CourseTO.MIME_TYPE.toLowerCase(), CourseTO.class);		
	}
	 
}
