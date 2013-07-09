package kornell.gui.client.presentation.course.notes;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class CourseNotesPlace extends Place {

	String courseUUID;

	public CourseNotesPlace(String courseUUID) {
		this.courseUUID = courseUUID;
	}

	public String getCourseUUID() {
		return courseUUID;
	}

	public void setCourseUUID(String courseUUID) {
		this.courseUUID = courseUUID;
	}

	@Prefix("notes")
	public static class Tokenizer implements PlaceTokenizer<CourseNotesPlace> {
		private static final String SEPARATOR = ";";

		public CourseNotesPlace getPlace(String token) {
			return new CourseNotesPlace(token);
		}

		public String getToken(CourseNotesPlace place) {
			return place.getCourseUUID();
		}
	}
}
