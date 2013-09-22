package kornell.gui.client.personnel;

import kornell.api.client.KornellClient;
import kornell.core.shared.data.Institution;
import kornell.gui.client.event.InstitutionEvent;
import kornell.gui.client.event.InstitutionEventHandler;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Timer;
import com.google.web.bindery.event.shared.EventBus;

public class Dean implements InstitutionEventHandler{

	KornellClient client;
	
	public Dean(EventBus bus, KornellClient client) { 
		this.client = client;
		bus.addHandler(InstitutionEvent.TYPE, this);
	}

	@Override
	public void onEnter(InstitutionEvent event) {
		GWT.log("Trick or Dean!");
		Timer timer = new Timer() {
			@Override
			public void run() {
				updateFavicon("skins/first/favicon.ico");
				GWT.log("Favicon set to default");
			}
		};
		try {
			updateFavicon(event.getInstitution().getAssetsURL() + "favicon.ico");
		} catch (Exception e) {
			// If it was somehow unable to fetch the favicon, use the default logo
			timer.schedule(2000);
		}
	}

	private static native void updateFavicon(String url) /*-{
		$wnd.updateFavicon(url);
	}-*/;
}
