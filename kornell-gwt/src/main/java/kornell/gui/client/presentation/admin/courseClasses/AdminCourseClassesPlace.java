package kornell.gui.client.presentation.admin.courseClasses;

import kornell.gui.client.presentation.admin.AdminPlace;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class AdminCourseClassesPlace extends AdminPlace{
	public static final AdminCourseClassesPlace instance = new AdminCourseClassesPlace();

	public AdminCourseClassesPlace() {
	}

	@Prefix("a.courseClasses")
	public static class Tokenizer implements PlaceTokenizer<AdminCourseClassesPlace> {
		public AdminCourseClassesPlace getPlace(String token) {
			return new AdminCourseClassesPlace();
		}

		public String getToken(AdminCourseClassesPlace place) {
			return "";
		}
	}

	@Override
	public String toString() {		
		return new Tokenizer().getToken(this);
	}
}
