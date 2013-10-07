package kornell.gui.client.presentation.profile;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class ProfilePlace extends Place{
	String userUUID;

	public ProfilePlace(String userUUID) {
		this.userUUID = userUUID;
	}

	public String getUserUUID() {
		return userUUID;
	}

	public void setUserUUID(String userUUID) {
		this.userUUID = userUUID;
	}

	@Prefix("profile")
	public static class Tokenizer implements PlaceTokenizer<ProfilePlace> {
		public ProfilePlace getPlace(String token) {
			return new ProfilePlace(token);
		}

		public String getToken(ProfilePlace place) {
			return place.getUserUUID();
		}
	}

	@Override
	public String toString() {		
		return new Tokenizer().getToken(this);
	}
}
