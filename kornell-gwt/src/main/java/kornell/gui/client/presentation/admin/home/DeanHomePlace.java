package kornell.gui.client.presentation.admin.home;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class DeanHomePlace extends Place{
	public static final DeanHomePlace instance = new DeanHomePlace();
	private String confirmation;
	private boolean userCreated;

	public DeanHomePlace(String confirmation) {
		this.confirmation = confirmation;
	}

	public DeanHomePlace() {
		this.confirmation = "";
	}

	public String getConfirmation() {
		return confirmation;
	}

	public void setConfirmation(String confirmation) {
		this.confirmation = confirmation;
	}

	@Prefix("deanHome")
	public static class Tokenizer implements PlaceTokenizer<DeanHomePlace> {
		public DeanHomePlace getPlace(String token) {
			return new DeanHomePlace(token);
		}

		public String getToken(DeanHomePlace place) {
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
