package kornell.gui.client.presentation.course.details;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class CourseDetailsPlace extends Place{

	String courseClassUUID;

	public CourseDetailsPlace(String courseClassUUID) {
		this.courseClassUUID = courseClassUUID;
	}
	
	public String getCourseClassUUID() {
		return courseClassUUID;
	}

	public void setCourseClassUUID(String courseClassUUID) {
		this.courseClassUUID = courseClassUUID;
	}
	
	@Prefix("details")
	public static class Tokenizer implements PlaceTokenizer<CourseDetailsPlace> {
		private static final String SEPARATOR = ";";

		public CourseDetailsPlace getPlace(String token) {
			return new CourseDetailsPlace(token);
		}

		public String getToken(CourseDetailsPlace place) {
			return place.getCourseClassUUID();
		}
	}
}



