package kornell.gui.client.personnel;

import java.util.List;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.Enrollment;
import kornell.core.entity.Institution;
import kornell.core.to.CourseClassTO;
import kornell.core.to.CourseClassesTO;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.event.ProgressEvent;
import kornell.gui.client.event.ProgressEventHandler;

import com.google.gwt.dom.client.Document;
import com.google.web.bindery.event.shared.EventBus;

public class Dean implements ProgressEventHandler{
	
	private String ICON_NAME = "favicon.ico";
	private String DEFAULT_SITE_TITLE = "Kornell";
	
	private static Dean instance;
	private EventBus bus;
	private KornellSession session;

	private Institution institution;
	private CourseClassTO courseClassTO;
	private CourseClassesTO courseClassesTO; 

	public static Dean getInstance() {
	   return instance;
	}

	public static void init(KornellSession session, EventBus bus, Institution institution){
	   instance = new Dean(session, bus, institution);
	}
	
	private Dean(KornellSession session, EventBus bus, Institution institution) { 
		this.bus = bus;
		this.institution = institution;
		this.session = session;
		
		bus.addHandler(ProgressEvent.TYPE, this);
		
		String url = institution.getAssetsURL();
		if(url != null){
			updateFavicon(url + ICON_NAME);
		} else {
			setDefaultFavicon();
		}
		
		String name = institution.getFullName();
		if(name != null){
			Document.get().setTitle(name);
		} else {
			Document.get().setTitle(DEFAULT_SITE_TITLE);
		}
	}
	
	private void setDefaultFavicon(){
		updateFavicon("skins/first/" + ICON_NAME);
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

	@Override
	public void onProgress(ProgressEvent event) {
		UserInfoTO user = session.getCurrentUser();
		Enrollment enrollment =  courseClassTO.getEnrollment();
		enrollment.setProgress(event.getProgressPercent());
		session.updateEnrollment(enrollment, new Callback<Enrollment>() {
			@Override
			public void ok(Enrollment enrollmentUpdated) {
				//
			}
		});
	}

	public Institution getInstitution() {
		return institution;
	}

	public CourseClassTO getCourseClassTO() {
		return courseClassTO;
	}

	public void setCourseClassTO(CourseClassTO courseClassTO) {
		this.courseClassTO = courseClassTO;
	}
	
	public void setCourseClassTO(String uuid){
		for (CourseClassTO courseClassTO : courseClassesTO.getCourseClasses()) {
			if(courseClassTO.getCourseClass().getUUID().equals(uuid)){
				this.courseClassTO = courseClassTO;
				return;
			}
		}
	}
	
	public CourseClassesTO getCourseClassesTO() {
		return courseClassesTO;
	}

	public void setCourseClassesTO(CourseClassesTO courseClassesTO) {
		this.courseClassesTO = courseClassesTO;
	}

}
