package kornell.api.client;

import java.util.HashMap;
import java.util.Map;

import kornell.core.entity.ActomEntries;
import kornell.core.entity.ChatThread;
import kornell.core.entity.Course;
import kornell.core.entity.CourseClass;
import kornell.core.entity.CourseVersion;
import kornell.core.entity.Enrollment;
import kornell.core.entity.Institution;
import kornell.core.entity.People;
import kornell.core.entity.Person;
import kornell.core.entity.Roles;
import kornell.core.lom.Contents;
import kornell.core.to.ChatThreadMessagesTO;
import kornell.core.to.CourseClassTO;
import kornell.core.to.CourseClassesTO;
import kornell.core.to.CourseVersionTO;
import kornell.core.to.CourseVersionsTO;
import kornell.core.to.CoursesTO;
import kornell.core.to.EnrollmentsTO;
import kornell.core.to.InstitutionRegistrationPrefixesTO;
import kornell.core.to.LibraryFilesTO;
import kornell.core.to.RolesTO;
import kornell.core.to.UnreadChatThreadsTO;
import kornell.core.to.UserHelloTO;
import kornell.core.to.UserInfoTO;

public class MediaTypes {
	
	static final MediaTypes instance = new MediaTypes();
	
	Map<String,Class<?>> type2class = new HashMap<String, Class<?>>();
	Map<Class<?>,String> class2type = new HashMap<Class<?>, String>();
	
	public MediaTypes() {
		register(People.TYPE, People.class);
		register(Person.TYPE, Person.class);
		register(Course.TYPE, Course.class);
		register(CoursesTO.TYPE, CoursesTO.class);
		register(CourseClassesTO.TYPE, CourseClassesTO.class);
		register(CourseClassTO.TYPE, CourseClassTO.class);
		register(CourseClass.TYPE, CourseClass.class);
		register(CourseVersionsTO.TYPE, CourseVersionsTO.class);
		register(CourseVersionTO.TYPE, CourseVersionTO.class);
		register(CourseVersion.TYPE, CourseVersion.class);
		register(UserInfoTO.TYPE,UserInfoTO.class);
		register(UserHelloTO.TYPE,UserHelloTO.class);
		register(Institution.TYPE,Institution.class);
		register(Roles.TYPE,Roles.class);
		register(RolesTO.TYPE,RolesTO.class);
		register(Contents.TYPE,Contents.class);
		register(Enrollment.TYPE,Enrollment.class);
		register(EnrollmentsTO.TYPE,EnrollmentsTO.class);
		register(ActomEntries.TYPE,ActomEntries.class);
		register(LibraryFilesTO.TYPE,LibraryFilesTO.class);
		register(ChatThread.TYPE,ChatThread.class);
		register(UnreadChatThreadsTO.TYPE,UnreadChatThreadsTO.class);
		register(ChatThreadMessagesTO.TYPE,ChatThreadMessagesTO.class);
		register(InstitutionRegistrationPrefixesTO.TYPE,InstitutionRegistrationPrefixesTO.class);
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
