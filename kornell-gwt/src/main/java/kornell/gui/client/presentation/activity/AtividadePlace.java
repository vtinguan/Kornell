package kornell.gui.client.presentation.activity;

import java.util.StringTokenizer;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.google.gwt.thirdparty.javascript.jscomp.mozilla.rhino.Token;

public class AtividadePlace extends Place {
	String packageURL;
	Integer item;

	public AtividadePlace(String url, Integer item) {
		this.packageURL = url;
		this.item = item;
	}

	public String getPackageURL() {
		return packageURL;
	}

	public Integer getItem() {
		return item;
	}
	
	public AtividadePlace next(){
		return new AtividadePlace(packageURL,item+1);
	}
	
	public AtividadePlace previous(){
		return new AtividadePlace(packageURL,item-1);
	}
	
	

	@Prefix("activity")
	public static class Tokenizer implements PlaceTokenizer<AtividadePlace> {
		private static final String SEPARATOR = ";";

		public AtividadePlace getPlace(String token) {
			String[] tokens = token.split(SEPARATOR);
			String packageUrl = tokens[0];
			Integer item = Integer.valueOf(
					tokens.length > 1 ?
							tokens[1] : "0");
			return new AtividadePlace(packageUrl, item);
		}

		public String getToken(AtividadePlace place) {
			return place.packageURL
					+ SEPARATOR
					+ place.getItem();
		}
	}



	public void setItem(Integer item) {
		this.item = item;
		
	}
}