package kornell.gui.client.presentation.atividade;

import static java.lang.Math.max;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class AtividadePlace extends Place {
	String courseUUID;
	Integer position;

	public AtividadePlace(String courseUUID, Integer position) {
		this.courseUUID = courseUUID;
		this.position = position;
	}

	public AtividadePlace next() {
		return new AtividadePlace(courseUUID, position + 1);
	}

	public AtividadePlace previous() {
		return new AtividadePlace(courseUUID, max(0,position - 1));
	}

	public String getCourseUUID() {
		return courseUUID;
	}

	public void setCourseUUID(String courseUUID) {
		this.courseUUID = courseUUID;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	@Prefix("activity")
	public static class Tokenizer implements PlaceTokenizer<AtividadePlace> {
		private static final String SEPARATOR = ";";

		public AtividadePlace getPlace(String token) {
			String[] tokens = token.split(SEPARATOR);
			String packageUrl = tokens[0];
			Integer item = Integer.valueOf(tokens.length > 1 ? tokens[1] : "0");
			return new AtividadePlace(packageUrl, item);
		}

		public String getToken(AtividadePlace place) {
			return place.getCourseUUID() + SEPARATOR + place.getPosition();
		}
	}

}