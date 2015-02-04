package kornell.api.client;

import kornell.core.entity.CourseClass;
import kornell.core.to.CourseClassesTO;

public class CourseClassesClient extends RESTClient {
	
	public void create(CourseClass courseClass, Callback<CourseClass> cb) {
		POST("/courseClasses").withContentType(CourseClass.TYPE).withEntityBody(courseClass).go(cb);
	}

	public void getCourseClassesTOByInstitution(String institutionUUID, Callback<CourseClassesTO> cb) {
		GET("/courseClasses?institutionUUID="+institutionUUID).sendRequest(null, cb);
	}

	public void getAdministratedCourseClassesTO(Callback<CourseClassesTO> cb) {
		getAdministratedCourseClassesByCourseVersion("", cb);
	}

	public void getAdministratedCourseClassesByCourseVersion(String courseVersionUUID, Callback<CourseClassesTO> cb) {
		GET("/courseClasses/administrated?courseVersionUUID="+courseVersionUUID).sendRequest(null, cb);
	}

}
