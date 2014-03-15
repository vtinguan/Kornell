package kornell.api.client;

import kornell.core.entity.CourseClass;
import kornell.core.lom.Contents;

public class CourseClassClient extends RESTClient {

	private String courseClassUUID;

	public CourseClassClient(String courseClassUUID) {
		this.courseClassUUID = courseClassUUID;
	}

	public void update(CourseClass courseClass, Callback<CourseClass> cb) {
		PUT("/courseClasses/" + courseClass.getUUID()).withContentType(CourseClass.TYPE).withEntityBody(courseClass).go(cb);
	}

	public void contents(Callback<Contents> callback) {
		GET("courseClasses",courseClassUUID,"contents").go(callback);
	}

}
