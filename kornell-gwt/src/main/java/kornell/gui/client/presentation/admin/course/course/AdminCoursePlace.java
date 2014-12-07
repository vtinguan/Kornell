package kornell.gui.client.presentation.admin.course.course;

import kornell.gui.client.presentation.admin.course.CoursePlace;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class AdminCoursePlace extends CoursePlace{
	public static final AdminCoursePlace instance = new AdminCoursePlace();

	public AdminCoursePlace() {
	}

	@Prefix("a.course")
	public static class Tokenizer implements PlaceTokenizer<AdminCoursePlace> {
		public AdminCoursePlace getPlace(String token) {
			return new AdminCoursePlace();
		}

		public String getToken(AdminCoursePlace place) {
			return "";
		}
	}

	@Override
	public String toString() {		
		return new Tokenizer().getToken(this);
	}
}
