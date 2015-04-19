package kornell.api.client;

import kornell.core.entity.Course;
import kornell.core.to.CoursesTO;

public class CoursesClient extends RESTClient {

	public void get(Callback<CoursesTO> callback) {
		get(true, callback);
	}

	public void get(boolean fetchChildCourses, Callback<CoursesTO> callback) {
		GET("/courses?fetchChildCourses="+fetchChildCourses).go(callback);
	}
	
	public void create(Course course, Callback<Course> callback) {
		POST("/courses").withContentType(Course.TYPE).withEntityBody(course).go(callback);
	}
	
}
