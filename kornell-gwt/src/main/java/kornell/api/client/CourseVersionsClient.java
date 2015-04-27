package kornell.api.client;

import kornell.core.entity.CourseVersion;
import kornell.core.to.CourseClassesTO;
import kornell.core.to.CourseVersionsTO;
import kornell.core.util.StringUtils;

public class CourseVersionsClient extends RESTClient {

	public void findByCourse(String courseUUID, String ps, String pn, String searchTerm, Callback<CourseVersionsTO> callback) {
		GET("/courseVersions" + "?ps=" + ps + "&pn=" + pn + "&searchTerm=" + searchTerm +
				(StringUtils.isSome(courseUUID) ? "&courseUUID=" + courseUUID : "")
			).go(callback);
	}
	
	public void findByCourse(String courseUUID, Callback<CourseVersionsTO> callback) {
		findByCourse(courseUUID, ""+Integer.MAX_VALUE, "1", "", callback);
	}
	
	public void get(String ps, String pn, String searchTerm, Callback<CourseVersionsTO> callback) {
		findByCourse(null, ps, pn, searchTerm, callback);
	}
	
	public void get(Callback<CourseVersionsTO> callback) {
		findByCourse("", ""+Integer.MAX_VALUE, "1", "", callback);
	}
	
	public void create(CourseVersion courseVersion, Callback<CourseVersion> callback) {
		POST("/courseVersions").withContentType(CourseVersion.TYPE).withEntityBody(courseVersion).go(callback);
	}
	
}
