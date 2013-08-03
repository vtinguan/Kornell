package kornell.api.client;

import java.util.HashMap;

import kornell.core.shared.data.CourseTO;
import kornell.core.shared.data.CoursesTO;
import kornell.core.shared.to.UserInfoTO;

public class MediaTypes extends HashMap<String,Class<?>>{
	private static final long serialVersionUID = 8495421711509512346L;

	public MediaTypes() {
		register(CoursesTO.MIME_TYPE, CoursesTO.class);
		register(CourseTO.MIME_TYPE, CourseTO.class);
		register(UserInfoTO.TYPE,UserInfoTO.class);
	}

	private void register(String type, Class<?> clazz) {
		put(type.toLowerCase(),clazz);		
	}
	 
}
