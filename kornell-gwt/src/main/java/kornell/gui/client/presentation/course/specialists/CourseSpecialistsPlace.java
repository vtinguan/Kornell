package kornell.gui.client.presentation.course.specialists;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class CourseSpecialistsPlace extends Place{

	String courseUUID;

	public CourseSpecialistsPlace(String courseUUID) {
		this.courseUUID = courseUUID;
	}
	
	public String getCourseUUID() {
		return courseUUID;
	}

	public void setCourseUUID(String courseUUID) {
		this.courseUUID = courseUUID;
	}
	
	@Prefix("Specialists")
	public static class Tokenizer implements PlaceTokenizer<CourseSpecialistsPlace> {
		private static final String SEPARATOR = ";";

		public CourseSpecialistsPlace getPlace(String token) {
			return new CourseSpecialistsPlace(token);
		}

		public String getToken(CourseSpecialistsPlace place) {
			return place.getCourseUUID();
		}
	}
}



