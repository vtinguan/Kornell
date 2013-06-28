package kornell.gui.client.presentation.course;

import kornell.core.shared.data.Course;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class CoursePlace extends Place{

	String courseUUID;

	public CoursePlace(String courseUUID) {
		this.courseUUID = courseUUID;
	}
	
	public String getCourseUUID() {
		return courseUUID;
	}

	public void setCourseUUID(String courseUUID) {
		this.courseUUID = courseUUID;
	}
	
	@Prefix("course")
	public static class Tokenizer implements PlaceTokenizer<CoursePlace> {
		private static final String SEPARATOR = ";";

		public CoursePlace getPlace(String token) {
			return new CoursePlace(token);
		}

		public String getToken(CoursePlace place) {
			return place.getCourseUUID();
		}
	}
}



