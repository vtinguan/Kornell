package kornell.gui.client.presentation.admin.courseversion.courseversions;

import kornell.gui.client.presentation.admin.courseversion.CourseVersionPlace;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class AdminCourseVersionsPlace extends CourseVersionPlace {
	public static final AdminCourseVersionsPlace instance = new AdminCourseVersionsPlace();

	public AdminCourseVersionsPlace() {
	}

	@Prefix("a.courseVersions")
	public static class Tokenizer implements PlaceTokenizer<AdminCourseVersionsPlace> {
		public AdminCourseVersionsPlace getPlace(String token) {
			return new AdminCourseVersionsPlace();
		}

		public String getToken(AdminCourseVersionsPlace place) {
			return "";
		}
	}

	@Override
	public String toString() {		
		return new Tokenizer().getToken(this);
	}
}
