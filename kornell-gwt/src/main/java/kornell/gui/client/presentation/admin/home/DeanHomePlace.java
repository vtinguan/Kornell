package kornell.gui.client.presentation.admin.home;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class DeanHomePlace extends Place{
	public static final DeanHomePlace instance = new DeanHomePlace();

	public DeanHomePlace() {
	}

	@Prefix("deanHome")
	public static class Tokenizer implements PlaceTokenizer<DeanHomePlace> {
		public DeanHomePlace getPlace(String token) {
			return new DeanHomePlace();
		}

		public String getToken(DeanHomePlace place) {
			return "";
		}
	}

	@Override
	public String toString() {		
		return new Tokenizer().getToken(this);
	}
}
