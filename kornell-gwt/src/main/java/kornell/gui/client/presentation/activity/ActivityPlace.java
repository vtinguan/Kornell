package kornell.gui.client.presentation.activity;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class ActivityPlace extends Place {
	String packageURL;
	
	public ActivityPlace(String url) {
		this.packageURL=url;
	}
	
	public String getPackageURL() {
		return packageURL;
	}



	@Prefix("activity")
	public static class Tokenizer implements PlaceTokenizer<ActivityPlace> {

		public ActivityPlace getPlace(String token) {
			return new ActivityPlace(token);
		}

		public String getToken(ActivityPlace place) {
			return place.packageURL;
		}
	}
}