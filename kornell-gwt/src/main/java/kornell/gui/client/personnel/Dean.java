package kornell.gui.client.personnel;

import kornell.api.client.KornellClient;
import kornell.gui.client.ClientFactory;

public class Dean {
	
	KornellClient client;
	
	private String ICON_NAME = "favicon.ico";
	private String DEFAULT_SITE_TITLE = "Kornell";
	
	public Dean(ClientFactory clientFactory) { 
		this.client = clientFactory.getKornellClient();		
		
		String url = clientFactory.getInstitution().getAssetsURL();
		if(url != null){
			updateFavicon(url + ICON_NAME);
		} else {
			setDefaultFavicon();
		}
		
		String name = clientFactory.getInstitution().getName();
		if(name != null){
			updateTitle(name);
		} else {
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
