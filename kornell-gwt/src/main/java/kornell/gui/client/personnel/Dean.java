package kornell.gui.client.personnel;

import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.core.shared.data.Institution;
import kornell.gui.client.event.InstitutionEvent;
import kornell.gui.client.event.InstitutionEventHandler;
import kornell.gui.client.util.ClientProperties;

import com.google.gwt.core.shared.GWT;
import com.google.web.bindery.event.shared.EventBus;

public class Dean implements InstitutionEventHandler{
	
	KornellClient client;

	private String ICON_NAME = "favicon.ico";
	private String DEFAULT_SITE_TITLE = "Kornell";
	
	public Dean(EventBus bus, KornellClient client) { 
		this.client = client;		
		
		String url = ClientProperties.getDecoded(ClientProperties.INSTITUTION_ASSETS_URL);
		if(url != null){
			updateFavicon(url + ICON_NAME);
		} else {
			setDefaultFavicon();
		}
		
		String name = ClientProperties.getDecoded(ClientProperties.INSTITUTION_NAME);
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
			ClientProperties.setEncoded(ClientProperties.INSTITUTION_ASSETS_URL, event.getInstitution().getAssetsURL());
			ClientProperties.setEncoded(ClientProperties.INSTITUTION_NAME, event.getInstitution().getName());
			updateFavicon(event.getInstitution().getAssetsURL() + ICON_NAME);
			updateTitle(event.getInstitution().getName());
		} catch (Exception e) {
			setDefaultFavicon();
			setDefaultTitle();
		}
	}
	
	private void setDefaultFavicon(){
		updateFavicon("skins/first/" + ICON_NAME);
	}
	
	private void setDefaultTitle(){
		updateTitle(DEFAULT_SITE_TITLE);
	}

	private static native void updateFavicon(String url) /*-{
		var link = document.createElement('link'),
		oldLink = document.getElementById('icon');
		link.id = 'icon';
		link.rel = 'shortcut icon';
		link.type = 'image/x-icon';
		link.href = url;
		if (oldLink) {
		 	$wnd.document.head.removeChild(oldLink);
		}
		$wnd.document.getElementsByTagName('head')[0].appendChild(link);
	}-*/;

	private static native void updateTitle(String title) /*-{
		$wnd.document.title = title;
	}-*/;
}
