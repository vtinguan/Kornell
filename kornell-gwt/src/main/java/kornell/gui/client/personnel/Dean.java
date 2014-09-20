package kornell.gui.client.personnel;

import java.util.ArrayList;
import java.util.List;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.Institution;
import kornell.core.to.CourseClassTO;
import kornell.core.to.CourseClassesTO;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.event.CourseClassesFetchedEvent;
import kornell.gui.client.event.LoginEvent;
import kornell.gui.client.event.LoginEventHandler;
import kornell.gui.client.event.LogoutEvent;
import kornell.gui.client.event.LogoutEventHandler;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.Timer;
import com.google.web.bindery.event.shared.EventBus;


public class Dean implements LoginEventHandler, LogoutEventHandler{
	
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
	
	private Dean(KornellSession session, EventBus bus, final Institution institution) { 
		this.bus = bus;
		this.institution = institution;
		this.session = session;
		bus.addHandler(LoginEvent.TYPE, this);
		bus.addHandler(LogoutEvent.TYPE, this);
		
		//get the skin and logo immediately
		updateSkin(institution.getSkin());
  	initInstitutionSkin(institution);
		
		//defer the course classes call
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
		    @Override
		    public void execute() {
		    	getUserCourseClasses();
		    }
		});

		
		showBody(false);
		Timer mdaTimer = new Timer() {
			public void run() {
				showBody(true);
			}
		};
		//wait 3 secs for the theme css
		mdaTimer.schedule((int) (3 * 1000));
	}

	private void initInstitutionSkin(Institution institution) {
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
		var link = $wnd.document.createElement('link'),
		oldLink = $wnd.document.getElementById('icon');
		link.id = 'icon';
		link.rel = 'shortcut icon';
		link.type = 'image/x-icon';
		link.href = url;
		if (oldLink) {
		 	$wnd.document.head.removeChild(oldLink);
		}
		$wnd.document.getElementsByTagName('head')[0].appendChild(link);
	}-*/;

	private static native void updateSkin(String skinName) /*-{
		var link = $wnd.document.createElement('link'),
		oldLink = $wnd.document.getElementById('Skin');
		link.id = 'Skin';
		link.rel = 'stylesheet';
		link.type = 'text/css';
		link.href = 'skins/first/css/skin'+ (skinName ? skinName : '') + '.nocache.css';
		if (oldLink) {
		 	$wnd.document.head.removeChild(oldLink);
		}
		$wnd.document.getElementsByTagName('head')[0].appendChild(link);
	}-*/;

	
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
	
	public List<CourseClassTO> getHelpCourseClasses(){
		List<CourseClassTO> courseClasses = new ArrayList<CourseClassTO>();
		for (CourseClassTO courseClassTO : courseClassesTO.getCourseClasses()) {
			if(courseClassTO.getEnrollment() != null && !courseClassTO.getCourseClass().isInvisible()){
				courseClasses.add(courseClassTO);
			}
		}
		return courseClasses;
	}
	
	public CourseClassesTO getCourseClassesTO() {
		return courseClassesTO;
	}

	public void setCourseClassesTO(CourseClassesTO courseClassesTO) {
		this.courseClassesTO = courseClassesTO;
		bus.fireEvent(new CourseClassesFetchedEvent());
	}
	
	private void getUserCourseClasses(){
		final Callback<CourseClassesTO> courseClassesCallback = new Callback<CourseClassesTO>() {
			@Override
			public void ok(final CourseClassesTO courseClasses) {
				Dean.getInstance().setCourseClassesTO(courseClasses);
			}
		};
		if (session.isAuthenticated() && session.isRegistered()) {
			session.courseClasses().getCourseClassesTOByInstitution(Dean.getInstance().getInstitution().getUUID(), courseClassesCallback);
		}
	}

	@Override
  public void onLogin(UserInfoTO user) {
		//getUserCourseClasses();
  }
	
	private static native void showBody(boolean show) /*-{
		$wnd.document.getElementsByTagName('body')[0].setAttribute('style', 'display: ' + (show ? 'block' : 'none'));
	}-*/;

	@Override
  public void onLogout() {
	  showBody(false);
  }

}
