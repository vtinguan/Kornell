package kornell.api.client;

import kornell.core.shared.data.Contents;

public class CourseClient extends HTTPClient {

	private String courseUUID;

	public CourseClient(String courseUUID) {
		this.courseUUID = courseUUID;
	}

	public void contents(Callback<Contents> callback) {
		GET("courses",courseUUID,"contents").send(callback);
	}

}
