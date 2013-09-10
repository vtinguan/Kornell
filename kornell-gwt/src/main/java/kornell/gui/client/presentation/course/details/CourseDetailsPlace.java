package kornell.gui.client.presentation.course.details;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class CourseDetailsPlace extends Place{

	String courseUUID;

	public CourseDetailsPlace(String courseUUID) {
		this.courseUUID = courseUUID;
	}
	
	public String getCourseUUID() {
		return courseUUID;
	}

	public void setCourseUUID(String courseUUID) {
		this.courseUUID = courseUUID;
	}
	
	@Prefix("details")
	public static class Tokenizer implements PlaceTokenizer<CourseDetailsPlace> {
		private static final String SEPARATOR = ";";

		public CourseDetailsPlace getPlace(String token) {
			return new CourseDetailsPlace(token);
		}

		public String getToken(CourseDetailsPlace place) {
			return place.getCourseUUID();
		}
	}
}



