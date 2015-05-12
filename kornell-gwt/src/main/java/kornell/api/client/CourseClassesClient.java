package kornell.api.client;

import kornell.core.entity.CourseClass;
import kornell.core.to.CourseClassesTO;

public class CourseClassesClient extends RESTClient {
	
	public void create(CourseClass courseClass, Callback<CourseClass> callback) {
		POST("/courseClasses").withContentType(CourseClass.TYPE).withEntityBody(courseClass).go(callback);
	}

	public void getCourseClassesTO(Callback<CourseClassesTO> cb) {
		GET("/courseClasses").sendRequest(null, cb);
	}

	public void getAdministratedCourseClassesTO(Callback<CourseClassesTO> cb) {
		getAdministratedCourseClassesTOByCourseVersionPaged("", ""+Integer.MAX_VALUE, "1", "", cb);
	}

	public void getAdministratedCourseClassesTOPaged(String ps, String pn, String searchTerm, Callback<CourseClassesTO> cb) {
		getAdministratedCourseClassesTOByCourseVersionPaged("", ps, pn, searchTerm, cb);
	}

	public void getAdministratedCourseClassesTOByCourseVersion(String courseVersionUUID, Callback<CourseClassesTO> cb) {
		getAdministratedCourseClassesTOByCourseVersionPaged(courseVersionUUID, ""+Integer.MAX_VALUE, "1", "", cb);
	}

	public void getAdministratedCourseClassesTOByCourseVersionPaged(String courseVersionUUID, String ps, String pn, String searchTerm, Callback<CourseClassesTO> cb) {
		GET("/courseClasses/administrated?courseVersionUUID="+courseVersionUUID + "&ps=" + ps + "&pn=" + pn + "&searchTerm=" + searchTerm).sendRequest(null, cb);
	}

}
