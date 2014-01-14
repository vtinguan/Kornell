package kornell.gui.client.personnel;

import java.math.BigDecimal;
import java.util.List;

import kornell.api.client.Callback;
import kornell.core.entity.Enrollment;
import kornell.core.entity.Institution;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.ClientFactory;
import kornell.gui.client.event.ProgressChangeEvent;
import kornell.gui.client.event.ProgressChangeEventHandler;

public class Dean implements ProgressChangeEventHandler{
	
	private static Dean instance;
	
	private ClientFactory clientFactory;
	
	private String ICON_NAME = "favicon.ico";
	private String DEFAULT_SITE_TITLE = "Kornell";
	
	public static Dean createInstance(ClientFactory clientFactory){
		if(instance == null)
			instance = new Dean(clientFactory);
		return instance;
	}
	
	public static Dean getInstance(){
		return instance;
	}
	
	private Dean(ClientFactory clientFactory) { 
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

	@Override
	public void onProgressChange(ProgressChangeEvent event) {
		UserInfoTO user = clientFactory.getUserSession().getUserInfo();
		List<Enrollment> enrollments = user.getEnrollmentsTO().getEnrollments();
		for (Enrollment enrollment : enrollments) {
			if(clientFactory.getCurrentCourseClass().getCourseClass().getUUID().equals(enrollment.getCourseClassUUID())){
					enrollment.setProgress(event.getProgressPercent());
					clientFactory.getKornellClient().updateEnrollment(enrollment, new Callback<Enrollment>() {
						@Override
						public void ok(Enrollment enrollmentUpdated) {
							//
						}
					});
				break;
			}
		}
	}

}
