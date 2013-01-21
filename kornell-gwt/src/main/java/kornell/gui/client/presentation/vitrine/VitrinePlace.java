package kornell.gui.client.presentation.vitrine;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class VitrinePlace extends Place {
	public static final VitrinePlace instance = new VitrinePlace();
	@Prefix("vitrine")
	public static class Tokenizer implements PlaceTokenizer<VitrinePlace> {

		public VitrinePlace getPlace(String token) {
			return VitrinePlace.instance;
		}

		public String getToken(VitrinePlace place) {
			return "";
		}
	}
}
