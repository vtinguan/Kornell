package kornell.api.client;

import kornell.core.to.CourseVersionsTO;

public class CourseVersionsClient extends RESTClient {

	public void findByCourse(String courseUUID, Callback<CourseVersionsTO> callback) {
		GET("/courseVersions?courseUUID=" + courseUUID).go(callback);
	}
	
}
