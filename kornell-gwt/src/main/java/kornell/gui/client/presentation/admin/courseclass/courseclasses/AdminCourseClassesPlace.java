package kornell.gui.client.presentation.admin.courseclass.courseclasses;

import kornell.gui.client.presentation.admin.courseclass.CourseClassPlace;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class AdminCourseClassesPlace extends CourseClassPlace{
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
