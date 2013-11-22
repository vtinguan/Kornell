package kornell.api.client;

import kornell.core.lom.Contents;

public class CourseClassClient extends RESTClient {

	private String courseClassUUID;

	public CourseClassClient(String courseClassUUID) {
		this.courseClassUUID = courseClassUUID;
	}

	public void contents(Callback<Contents> callback) {
		GET("courseClasses",courseClassUUID,"contents").go(callback);
	}

}
