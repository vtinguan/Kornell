package kornell.gui.client.presentation.course.course;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class CourseHomePlace extends Place{

	String courseUUID;

	public CourseHomePlace(String courseUUID) {
		this.courseUUID = courseUUID;
	}
	
	public String getCourseUUID() {
		return courseUUID;
	}

	public void setCourseUUID(String courseUUID) {
		this.courseUUID = courseUUID;
	}
	
	@Prefix("course")
	public static class Tokenizer implements PlaceTokenizer<CourseHomePlace> {
		private static final String SEPARATOR = ";";

		public CourseHomePlace getPlace(String token) {
			return new CourseHomePlace(token);
		}

		public String getToken(CourseHomePlace place) {
			return place.getCourseUUID();
		}
	}
}



