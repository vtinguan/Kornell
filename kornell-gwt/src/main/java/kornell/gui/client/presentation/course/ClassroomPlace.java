package kornell.gui.client.presentation.course;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;


public class ClassroomPlace extends Place {
	String enrollmentUUID;


	public ClassroomPlace(String enrollmentUUID) {
		this.enrollmentUUID = enrollmentUUID;
	}

	public String getEnrollmentUUID() {
		return enrollmentUUID;
	}

	public void serEnrollmentUUID(String enrollmentUUID) {
		this.enrollmentUUID = enrollmentUUID;
	}

	@Prefix("classroom")
	public static class Tokenizer implements PlaceTokenizer<ClassroomPlace> {
		public ClassroomPlace getPlace(String token) {
			return new ClassroomPlace(token);
		}

		public String getToken(ClassroomPlace place) {
			return place.getEnrollmentUUID();
		}
	}

	@Override
	public String toString() {		
		return new Tokenizer().getToken(this);
	}
}