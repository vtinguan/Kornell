package kornell.gui.client.presentation.profile;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class ProfilePlace extends Place{
	String username;

	public ProfilePlace(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Prefix("profile")
	public static class Tokenizer implements PlaceTokenizer<ProfilePlace> {
		public ProfilePlace getPlace(String token) {
			return new ProfilePlace(token);
		}

		public String getToken(ProfilePlace place) {
			return place.getUsername();
		}
	}

	@Override
	public String toString() {		
		return new Tokenizer().getToken(this);
	}
}
