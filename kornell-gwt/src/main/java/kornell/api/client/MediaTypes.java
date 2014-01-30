package kornell.api.client;

import java.util.HashMap;
import java.util.Map;

import kornell.core.entity.ActomEntries;
import kornell.core.entity.Enrollment;
import kornell.core.entity.Enrollments;
import kornell.core.entity.Institution;
import kornell.core.entity.Registration;
import kornell.core.entity.Registrations;
import kornell.core.lom.Contents;
import kornell.core.to.CourseClassTO;
import kornell.core.to.CourseClassesTO;
import kornell.core.to.CourseTO;
import kornell.core.to.CourseVersionTO;
import kornell.core.to.CoursesTO;
import kornell.core.to.RegistrationsTO;
import kornell.core.to.S3PolicyTO;
import kornell.core.to.UserInfoTO;

public class MediaTypes {
	
	static final MediaTypes instance = new MediaTypes();
	
	Map<String,Class<?>> type2class = new HashMap<String, Class<?>>();
	Map<Class<?>,String> class2type = new HashMap<Class<?>, String>();
	
	public MediaTypes() {
		register(CourseTO.TYPE, CourseTO.class);
		register(CoursesTO.TYPE, CoursesTO.class);
		register(CourseClassesTO.TYPE, CourseClassesTO.class);
		register(CourseClassTO.TYPE, CourseClassTO.class);
		register(CourseVersionTO.TYPE, CourseVersionTO.class);
		register(UserInfoTO.TYPE,UserInfoTO.class);
		register(Registration.TYPE,Registration.class);
		register(Registrations.TYPE,Registrations.class);
		register(RegistrationsTO.TYPE,RegistrationsTO.class);
		register(Institution.TYPE,Institution.class);
		register(Contents.TYPE,Contents.class);
		register(Enrollment.TYPE,Enrollment.class);
		register(Enrollments.TYPE,Enrollments.class);
		register(S3PolicyTO.TYPE,S3PolicyTO.class);
		register(ActomEntries.TYPE,ActomEntries.class);
	}

	private void register(String type, Class<?> clazz) {
		type2class.put(type.toLowerCase(),clazz);
		class2type.put(clazz,type.toLowerCase());
	}
	
	public static MediaTypes get(){
		return instance;
	}	 
	
	public Class<?> classOf(String type){
		return type2class.get(type);
	}
	
	public String typeOf(Class<?> clazz){
		return class2type.get(clazz);
	}
	
	public boolean containsType(String type){
		return type2class.containsKey(type);
	}
	
	public boolean containsClass(Class<?> clazz){
		return class2type.containsKey(clazz);
	}
}
