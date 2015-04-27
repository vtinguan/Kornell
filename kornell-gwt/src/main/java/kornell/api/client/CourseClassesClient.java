package kornell.api.client;

import kornell.core.entity.CourseClass;
import kornell.core.to.CourseClassesTO;
import kornell.core.to.EnrollmentsTO;

public class CourseClassesClient extends RESTClient {
	
	public void create(CourseClass courseClass, Callback<CourseClass> callback) {
		POST("/courseClasses").withContentType(CourseClass.TYPE).withEntityBody(courseClass).go(callback);
	}

	public void getCourseClassesTOByInstitution(String institutionUUID, Callback<CourseClassesTO> cb) {
		GET("/courseClasses?institutionUUID="+institutionUUID).sendRequest(null, cb);
	}

	public void getAdministratedCourseClassesTOByInstitution(String institutionUUID, Callback<CourseClassesTO> cb) {
		getAdministratedCourseClassesTOByInstitution(institutionUUID, ""+Integer.MAX_VALUE, "1", "", cb);
	}

	public void getAdministratedCourseClassesTOByInstitution(String institutionUUID, String ps, String pn, String searchTerm, Callback<CourseClassesTO> cb) {
		GET("/courseClasses/administrated?institutionUUID="+institutionUUID + "&ps=" + ps + "&pn=" + pn + "&searchTerm=" + searchTerm).sendRequest(null, cb);
	}

}
