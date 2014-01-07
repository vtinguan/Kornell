package kornell.gui.client.presentation.admin.home;

import kornell.gui.client.presentation.admin.AdminPlace;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class AdminHomePlace extends AdminPlace{
	public static final AdminHomePlace instance = new AdminHomePlace();

	public AdminHomePlace() {
	}

	@Prefix("adminHome")
	public static class Tokenizer implements PlaceTokenizer<AdminHomePlace> {
		public AdminHomePlace getPlace(String token) {
			return new AdminHomePlace();
		}

		public String getToken(AdminHomePlace place) {
			return "";
		}
	}

	@Override
	public String toString() {		
		return new Tokenizer().getToken(this);
	}
}
