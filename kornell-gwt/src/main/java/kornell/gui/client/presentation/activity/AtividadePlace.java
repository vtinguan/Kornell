package kornell.gui.client.presentation.activity;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class AtividadePlace extends Place {
	String packageURL;
	
	public AtividadePlace(String url) {
		this.packageURL=url;
	}
	
	public String getPackageURL() {
		return packageURL;
	}



	@Prefix("activity")
	public static class Tokenizer implements PlaceTokenizer<AtividadePlace> {

		public AtividadePlace getPlace(String token) {
			return new AtividadePlace(token);
		}

		public String getToken(AtividadePlace place) {
			return place.packageURL;
		}
	}
}