package kornell.gui.client.presentation.admin.institution;

import kornell.gui.client.presentation.admin.AdminPlace;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class AdminInstitutionPlace extends AdminPlace{
	public static final AdminInstitutionPlace instance = new AdminInstitutionPlace();

	public AdminInstitutionPlace() {
	}

	@Prefix("a.institution")
	public static class Tokenizer implements PlaceTokenizer<AdminInstitutionPlace> {
		public AdminInstitutionPlace getPlace(String token) {
			return new AdminInstitutionPlace();
		}

		public String getToken(AdminInstitutionPlace place) {
			return "";
		}
	}

	@Override
	public String toString() {		
		return new Tokenizer().getToken(this);
	}
}
