package kornell.gui.client.presentation.admin.courseclass.courseclass;

import kornell.gui.client.presentation.admin.AdminPlace;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class AdminCourseClassPlace extends AdminPlace{
	private String courseClassUUID;

	public AdminCourseClassPlace(String courseClassUUID) {
		this.setCourseClassUUID(courseClassUUID);
	}

	@Prefix("a.courseClass")
	public static class Tokenizer implements PlaceTokenizer<AdminCourseClassPlace> {
		public AdminCourseClassPlace getPlace(String token) {
			return new AdminCourseClassPlace(token);
		}

		public String getToken(AdminCourseClassPlace place) {
			return place.getCourseClassUUID();
		}
	}

	@Override
	public String toString() {		
		return new Tokenizer().getToken(this);
	}

	public String getCourseClassUUID() {
	  return courseClassUUID;
  }

	public void setCourseClassUUID(String courseClassUUID) {
	  this.courseClassUUID = courseClassUUID;
  }
}
