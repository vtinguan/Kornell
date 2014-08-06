package kornell.gui.client.presentation.message;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class MessagePlace extends Place{

	public static final String INBOX = "inbox";
	public static final String ARCHIVED = "archived";
	public static final String COMPOSE = "compose";
	
	private String viewType;
	
	public MessagePlace(String viewType) {
		this.viewType = viewType;
	}

	public String getViewType() {
		return viewType;
	}

	public void setViewType(String viewType) {
		this.viewType = viewType;
	}

	@Prefix("message")
	public static class Tokenizer implements PlaceTokenizer<MessagePlace> {
		public MessagePlace getPlace(String tokIn) {
			String[] toks = tokIn.split(";");
			String viewType = toks.length > 0 ? toks[0] : "";
			return new MessagePlace(viewType);
		}

		public String getToken(MessagePlace place) {
			return place.getViewType();
		}
	}

	@Override
	public String toString() {		
		return new Tokenizer().getToken(this);
	}
}
