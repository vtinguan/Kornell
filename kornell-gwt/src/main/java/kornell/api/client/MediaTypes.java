package kornell.api.client;

import java.util.HashMap;

import kornell.core.entity.Institution;
import kornell.core.entity.Registration;
import kornell.core.entity.Registrations;
import kornell.core.lom.Contents;
import kornell.core.to.CourseTO;
import kornell.core.to.CoursesTO;
import kornell.core.to.RegistrationsTO;
import kornell.core.to.UserInfoTO;

public class MediaTypes extends HashMap<String,Class<?>>{
	private static final long serialVersionUID = 8495421711509512346L;

	public MediaTypes() {
		register(CoursesTO.TYPE, CoursesTO.class);
		register(CourseTO.TYPE, CourseTO.class);
		register(UserInfoTO.TYPE,UserInfoTO.class);
		register(Registration.TYPE,Registration.class);
		register(Registrations.TYPE,Registrations.class);
		register(RegistrationsTO.TYPE,RegistrationsTO.class);
		register(Institution.TYPE,Institution.class);
		register(Contents.TYPE,Contents.class);
	}

	private void register(String type, Class<?> clazz) {
		put(type.toLowerCase(),clazz);		
	}
	 
}
