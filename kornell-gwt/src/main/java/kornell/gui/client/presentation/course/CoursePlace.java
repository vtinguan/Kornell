package kornell.gui.client.presentation.course;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;


public class CoursePlace extends Place {
	String courseUUID;
	String actomKey;

	public CoursePlace(String courseUUID, String actomKey) {
		this.courseUUID = courseUUID;
		this.actomKey = actomKey;
	}

	public CoursePlace(String courseUUID) {
		this(courseUUID,"");
	}

	public String getCourseUUID() {
		return courseUUID;
	}

	public void setCourseUUID(String courseUUID) {
		this.courseUUID = courseUUID;
	}

	public String getActomKey() {
		return actomKey;
	}

	public void setActomKey(String actomKey) {
		this.actomKey = actomKey;
	}

	@Prefix("course")
	public static class Tokenizer implements PlaceTokenizer<CoursePlace> {
		private static final String SEPARATOR = ";";

		public CoursePlace getPlace(String token) {
			String[] tokens = token.split(SEPARATOR);
			String packageUrl = tokens[0];
			String actomKey = tokens.length > 1 ? tokens[1] : "";
			return new CoursePlace(packageUrl, actomKey);
		}

		public String getToken(CoursePlace place) {
			return place.getCourseUUID() + SEPARATOR + place.getActomKey();
		}
	}

}