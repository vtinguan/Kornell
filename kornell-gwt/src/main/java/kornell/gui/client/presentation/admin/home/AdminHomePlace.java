package kornell.gui.client.presentation.admin.home;

import kornell.gui.client.presentation.admin.AdminPlace;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class AdminHomePlace extends AdminPlace{
	private String courseClassUUID;

	public AdminHomePlace(String courseClassUUID) {
		this.setCourseClassUUID(courseClassUUID);
	}

	@Prefix("adminHome")
	public static class Tokenizer implements PlaceTokenizer<AdminHomePlace> {
		public AdminHomePlace getPlace(String token) {
			return new AdminHomePlace(token);
		}

		public String getToken(AdminHomePlace place) {
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
