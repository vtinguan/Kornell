package kornell.client.presenter.vitrine;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class VitrinePlace extends Place {
	@Prefix("vitrine")
	public static class Tokenizer implements PlaceTokenizer<VitrinePlace> {

		public VitrinePlace getPlace(String token) {
			return new VitrinePlace();
		}

		public String getToken(VitrinePlace place) {
			return "";
		}
	}
}
