package kornell.api.client;

import kornell.core.entity.CourseVersion;
import kornell.core.to.CourseVersionsTO;
import kornell.core.util.StringUtils;

public class CourseVersionsClient extends RESTClient {

	public void findByCourse(String courseUUID, Callback<CourseVersionsTO> callback) {
		GET("/courseVersions" + (StringUtils.isSome(courseUUID) ?
				"?courseUUID=" + courseUUID : 
				"")).go(callback);
	}

	public void get(Callback<CourseVersionsTO> callback) {
		findByCourse("", callback);
	}
	
	public void create(CourseVersion courseVersion, Callback<CourseVersion> callback) {
		POST("/courseVersions").withContentType(CourseVersion.TYPE).withEntityBody(courseVersion).go(callback);
	}
	
}
