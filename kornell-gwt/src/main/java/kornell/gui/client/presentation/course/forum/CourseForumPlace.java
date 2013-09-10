package kornell.gui.client.presentation.course.forum;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class CourseForumPlace extends Place{

	String courseUUID;

	public CourseForumPlace(String courseUUID) {
		this.courseUUID = courseUUID;
	}
	
	public String getCourseUUID() {
		return courseUUID;
	}

	public void setCourseUUID(String courseUUID) {
		this.courseUUID = courseUUID;
	}
	
	@Prefix("forum")
	public static class Tokenizer implements PlaceTokenizer<CourseForumPlace> {
		private static final String SEPARATOR = ";";

		public CourseForumPlace getPlace(String token) {
			return new CourseForumPlace(token);
		}

		public String getToken(CourseForumPlace place) {
			return place.getCourseUUID();
		}
	}
}



