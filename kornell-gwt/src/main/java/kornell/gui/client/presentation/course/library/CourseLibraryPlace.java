package kornell.gui.client.presentation.course.library;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class CourseLibraryPlace extends Place{

	String courseUUID;

	public CourseLibraryPlace(String courseUUID) {
		this.courseUUID = courseUUID;
	}
	
	public String getCourseUUID() {
		return courseUUID;
	}

	public void setCourseUUID(String courseUUID) {
		this.courseUUID = courseUUID;
	}
	
	@Prefix("library")
	public static class Tokenizer implements PlaceTokenizer<CourseLibraryPlace> {
		private static final String SEPARATOR = ";";

		public CourseLibraryPlace getPlace(String token) {
			return new CourseLibraryPlace(token);
		}

		public String getToken(CourseLibraryPlace place) {
			return place.getCourseUUID();
		}
	}
}



