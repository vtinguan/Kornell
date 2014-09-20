package kornell.api.client;

import kornell.core.entity.CourseClass;
import kornell.core.to.CourseClassesTO;

public class CourseClassesClient extends RESTClient {
	
	public void create(CourseClass courseClass, Callback<CourseClass> callback) {
		POST("/courseClasses").withContentType(CourseClass.TYPE).withEntityBody(courseClass).go(callback);
	}

	public void getCourseClassesTOByInstitution(String institutionUUID, Callback<CourseClassesTO> cb) {
		GET("/courseClasses?institutionUUID="+institutionUUID).sendRequest(null, cb);
	}

	public void getAdministratedCourseClassesTOByInstitution(String institutionUUID, Callback<CourseClassesTO> cb) {
		GET("/courseClasses/administrated?institutionUUID="+institutionUUID).sendRequest(null, cb);
	}

}
