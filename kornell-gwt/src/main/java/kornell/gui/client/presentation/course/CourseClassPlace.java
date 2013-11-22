package kornell.gui.client.presentation.course;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;


public class CourseClassPlace extends Place {
	String courseClassUUID;

	public CourseClassPlace(String courseClassUUID, String actomKey) {
		this.courseClassUUID = courseClassUUID;
	}

	public CourseClassPlace(String courseClassUUID) {
		this(courseClassUUID,"");
	}

	public String getCourseClassUUID() {
		return courseClassUUID;
	}

	public void setCourseClassUUID(String courseClassUUID) {
		this.courseClassUUID = courseClassUUID;
	}

	@Prefix("course")
	public static class Tokenizer implements PlaceTokenizer<CourseClassPlace> {
		public CourseClassPlace getPlace(String token) {
			return new CourseClassPlace(token);
		}

		public String getToken(CourseClassPlace place) {
			return place.getCourseClassUUID();
		}
	}

	@Override
	public String toString() {		
		return new Tokenizer().getToken(this);
	}
}