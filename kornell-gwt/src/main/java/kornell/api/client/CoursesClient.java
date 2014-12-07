package kornell.api.client;

import kornell.core.entity.Course;
import kornell.core.to.CoursesTO;

public class CoursesClient extends RESTClient {

	public void get(Callback<CoursesTO> callback) {
		GET("/courses").go(callback);
	}
	
	public void create(Course course, Callback<Course> callback) {
		POST("/courses").withContentType(Course.TYPE).withEntityBody(course).go(callback);
	}
	
}
