package kornell.gui.client.presentation.vitrine;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class VitrinePlace extends Place{
	public static final VitrinePlace instance = new VitrinePlace();
	private String confirmation;
	private boolean userCreated;

	public VitrinePlace(String confirmation) {
		this.confirmation = confirmation;
	}

	public VitrinePlace() {
		this.confirmation = "";
	}

	public String getConfirmation() {
		return confirmation;
	}

	public void setConfirmation(String confirmation) {
		this.confirmation = confirmation;
	}

	@Prefix("Vitrine")
	public static class Tokenizer implements PlaceTokenizer<VitrinePlace> {
		public VitrinePlace getPlace(String token) {
			return new VitrinePlace(token);
		}

		public String getToken(VitrinePlace place) {
			return place.getConfirmation();
		}
	}

	@Override
	public String toString() {		
		return new Tokenizer().getToken(this);
	}

	public boolean isUserCreated() {
		return userCreated;
	}

	public void setUserCreated(boolean userCreated) {
		this.userCreated = userCreated;
	}
}
