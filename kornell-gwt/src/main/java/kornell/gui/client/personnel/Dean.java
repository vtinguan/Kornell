package kornell.gui.client.personnel;

import kornell.api.client.Callback;
import kornell.core.entity.Enrollment;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.ClientFactory;
import kornell.gui.client.event.ProgressChangeEvent;
import kornell.gui.client.event.ProgressChangeEventHandler;

import com.google.gwt.dom.client.Document;

public class Dean implements ProgressChangeEventHandler{
	
	private ClientFactory clientFactory;
	
	private String ICON_NAME = "favicon.ico";
	private String DEFAULT_SITE_TITLE = "Kornell";
	
	public Dean(ClientFactory clientFactory) { 
		this.clientFactory = clientFactory;
		this.clientFactory.getEventBus().addHandler(ProgressChangeEvent.TYPE, this);
		
		String url = clientFactory.getInstitution().getAssetsURL();
		if(url != null){
			updateFavicon(url + ICON_NAME);
		} else {
			setDefaultFavicon();
		}
		
		String name = clientFactory.getInstitution().getFullName();
		if(name != null){
			//updateTitle(name);
			Document.get().setTitle(name);
		} else {
			Document.get().setTitle(DEFAULT_SITE_TITLE);
			//setDefaultTitle();
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

	@Override
	public void onProgressChange(ProgressChangeEvent event) {
		UserInfoTO user = clientFactory.getUserSession().getUserInfo();
		Enrollment enrollment =  clientFactory.getCurrentCourseClass().getEnrollment();
		enrollment.setProgress(event.getProgressPercent());
		clientFactory.getKornellClient().updateEnrollment(enrollment, new Callback<Enrollment>() {
			@Override
			public void ok(Enrollment enrollmentUpdated) {
				//
			}
		});
	}

}
