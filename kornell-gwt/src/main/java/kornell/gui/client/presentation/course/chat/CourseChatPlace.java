package kornell.gui.client.presentation.course.chat;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class CourseChatPlace extends Place{

	String courseUUID;

	public CourseChatPlace(String courseUUID) {
		this.courseUUID = courseUUID;
	}
	
	public String getCourseUUID() {
		return courseUUID;
	}

	public void setCourseUUID(String courseUUID) {
		this.courseUUID = courseUUID;
	}
	
	@Prefix("chat")
	public static class Tokenizer implements PlaceTokenizer<CourseChatPlace> {
		private static final String SEPARATOR = ";";

		public CourseChatPlace getPlace(String token) {
			return new CourseChatPlace(token);
		}

		public String getToken(CourseChatPlace place) {
			return place.getCourseUUID();
		}
	}
}



