package kornell.gui.client.personnel;

import kornell.api.client.KornellClient;
import kornell.gui.client.event.InstitutionEvent;
import kornell.gui.client.event.InstitutionEventHandler;
import kornell.gui.client.util.ClientProperties;

import com.google.gwt.core.shared.GWT;
import com.google.web.bindery.event.shared.EventBus;

public class Dean implements InstitutionEventHandler{
	
	KornellClient client;
	
	public Dean(EventBus bus, KornellClient client) { 
		this.client = client;
		
		String url = ClientProperties.getDecoded("institutionAssetsURL");
		if(url != null){
			updateFavicon(url + "favicon.ico");
		} else {
			setDefaultFavicon();
		}
		
		String name = ClientProperties.getDecoded("institutionName");
		if(name != null){
			updateTitle(name);
		} else {
			setDefaultTitle();
		}
		
		bus.addHandler(InstitutionEvent.TYPE, this);
	}

	@Override
	public void onEnter(InstitutionEvent event) {
		GWT.log("Trick or Dean!");
		try {
			ClientProperties.setEncoded("institutionAssetsURL", event.getInstitution().getAssetsURL());
			ClientProperties.setEncoded("institutionName", event.getInstitution().getName());
			updateFavicon(event.getInstitution().getAssetsURL() + "favicon.ico");
			updateTitle(event.getInstitution().getName());
		} catch (Exception e) {
			setDefaultFavicon();
			setDefaultTitle();
		}
	}
	
	private void setDefaultFavicon(){
		updateFavicon("skins/first/favicon.ico");
	}
	
	private void setDefaultTitle(){
		updateTitle("Kornell");
	}

	private static native void updateFavicon(String url) /*-{
		$wnd.updateFavicon(url);
	}-*/;

	private static native void updateTitle(String title) /*-{
		$wnd.document.title = title;
	}-*/;
}
