package kornell.gui.client.presentation.course.details;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class CourseDetailsPlace extends Place{

	String courseClassUUID;

	public CourseDetailsPlace(String courseClassUUID) {
		this.courseClassUUID = courseClassUUID;
	}
	
	public String getCourseUUID() {
		return courseClassUUID;
	}

	public void setCourseUUID(String courseClassUUID) {
		this.courseClassUUID = courseClassUUID;
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



