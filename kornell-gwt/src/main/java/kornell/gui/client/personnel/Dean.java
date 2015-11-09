package kornell.gui.client.personnel;

import java.util.ArrayList;
import java.util.List;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.Institution;
import kornell.core.to.CourseClassTO;
import kornell.core.to.CourseClassesTO;
import kornell.core.to.UnreadChatThreadTO;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.event.CourseClassesFetchedEvent;
import kornell.gui.client.event.LoginEvent;
import kornell.gui.client.event.LoginEventHandler;
import kornell.gui.client.event.LogoutEvent;
import kornell.gui.client.event.LogoutEventHandler;
import kornell.gui.client.event.UnreadMessagesCountChangedEvent;
import kornell.gui.client.event.UnreadMessagesCountChangedEventHandler;
import kornell.gui.client.event.UnreadMessagesPerThreadFetchedEvent;
import kornell.gui.client.event.UnreadMessagesPerThreadFetchedEventHandler;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.Timer;
import com.google.web.bindery.event.shared.EventBus;

public class Dean implements LoginEventHandler, LogoutEventHandler,
		UnreadMessagesPerThreadFetchedEventHandler,
		UnreadMessagesCountChangedEventHandler {

	private String ICON_NAME = "favicon.ico";
	private String DEFAULT_SITE_TITLE = "Kornell";

	private static Dean instance;
	private EventBus bus;
	private KornellSession session;

	private Institution institution;
	private CourseClassTO courseClassTO;
	private String courseClassUUID;
	private CourseClassesTO courseClassesTO;
	private int totalCount;

	public static Dean getInstance() {
		return instance;
	}

	public static void init(KornellSession session, EventBus bus,
			Institution institution) {
		instance = new Dean(session, bus, institution);
	}

	private Dean(KornellSession session, EventBus bus,
			final Institution institution) {
		this.bus = bus;
		this.institution = institution;
		this.session = session;
		bus.addHandler(LoginEvent.TYPE, this);
		bus.addHandler(LogoutEvent.TYPE, this);
		bus.addHandler(UnreadMessagesPerThreadFetchedEvent.TYPE, this);
		bus.addHandler(UnreadMessagesCountChangedEvent.TYPE, this);

		// get the skin and logo immediately
		// updateSkin(institution.getSkin()); //TODO re-enable skins
		initInstitutionSkin();

		// defer the course classes call
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
		// wait 3 secs for the theme css
		mdaTimer.schedule((int) (3 * 1000));
	}

	private void initInstitutionSkin() {
		String url = institution.getAssetsURL();
		if (url != null) {
			updateFavicon(url + ICON_NAME);
		} else {
			setDefaultFavicon();
		}

		String name = institution.getFullName();
		String title = DEFAULT_SITE_TITLE;
		if (name != null) {
			title = name;
			if (totalCount > 0)
				title = "(" + totalCount + ") " + name;
		}
		Document.get().setTitle(title);
	}

	private void setDefaultFavicon() {
		updateFavicon(ICON_NAME);
	}

	private static native void updateFavicon(String url) /*-{
		var link = $wnd.document.createElement('link'), oldLink = $wnd.document
				.getElementById('icon');
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
		var link = $wnd.document.createElement('link'), oldLink = $wnd.document
				.getElementById('kornellSkin');
		link.id = 'kornellSkin';
		link.rel = 'stylesheet';
		link.type = 'text/css';
		link.href = ClientConstants.CSS_PATH + (skinName ? skinName : '')
				+ '.nocache.css';
		if (oldLink) {
			$wnd.document.head.removeChild(oldLink);
		}
		$wnd.document.getElementsByTagName('head')[0].appendChild(link);
	}-*/;

	public Institution getInstitution() {
		return institution;
	}

	public Institution setInstitution(Institution institution) {
		return this.institution = institution;
	}

	public CourseClassTO getCourseClassTO() {
		return courseClassTO;
	}

	public void setCourseClassTO(CourseClassTO courseClassTO) {		
		this.courseClassTO = courseClassTO;
	}
	
	public void setCourseClassTO(String uuid) {
		this.courseClassUUID = uuid;
		refresh();
	}

	/*
	 * Method created because setCourseClassTO was being called before setCourseClassesTO
	 */
	public void refresh() {
		if (courseClassesTO != null) {
			List<CourseClassTO> courseClasses = courseClassesTO.getCourseClasses();
			for (CourseClassTO courseClassTO : courseClasses) {
				String iCourseClassUUID = courseClassTO.getCourseClass().getUUID();
				if (iCourseClassUUID.equals(courseClassUUID)) {
					this.courseClassTO = courseClassTO;
					return;
				}
			}
		}
		
	}

	public List<CourseClassTO> getHelpCourseClasses() {
		List<CourseClassTO> courseClasses = new ArrayList<CourseClassTO>();
		if (courseClassesTO != null) {
			for (CourseClassTO courseClassTO : courseClassesTO
					.getCourseClasses()) {
				if (courseClassTO.getEnrollment() != null
						&& !courseClassTO.getCourseClass().isInvisible()) {
					courseClasses.add(courseClassTO);
				}
			}
		}
		return courseClasses;
	}

	public CourseClassesTO getCourseClassesTO() {
		return courseClassesTO;
	}

	public void setCourseClassesTO(CourseClassesTO courseClassesTO) {
		this.courseClassesTO = courseClassesTO;
		refresh();
		bus.fireEvent(new CourseClassesFetchedEvent());		
	}

	private void getUserCourseClasses() {
		final Callback<CourseClassesTO> courseClassesCallback = new Callback<CourseClassesTO>() {
			@Override
			public void ok(final CourseClassesTO courseClasses) {
				Dean.getInstance().setCourseClassesTO(courseClasses);
			}
		};
		if (session.isAuthenticated()) {
			session.courseClasses().getCourseClassesTO(courseClassesCallback);
		}
	}

	@Override
	public void onLogin(UserInfoTO user) {
		// getUserCourseClasses();
	}

	@Override
	public void onUnreadMessagesPerThreadFetched(
			UnreadMessagesPerThreadFetchedEvent event) {
		int count = 0;
		for (UnreadChatThreadTO unreadChatThreadTO : event
				.getUnreadChatThreadTOs()) {
			count = count
					+ Integer.parseInt(unreadChatThreadTO.getUnreadMessages());
		}
		totalCount = count;
		initInstitutionSkin();
	}

	@Override
	public void onUnreadMessagesCountChanged(
			UnreadMessagesCountChangedEvent event) {
		totalCount = event.isIncrement() ? totalCount + event.getCountChange()
				: totalCount - event.getCountChange();
		initInstitutionSkin();
	}

	private static native void showBody(boolean show) /*-{
		$wnd.document.getElementsByTagName('body')[0].setAttribute('style',
				'display: ' + (show ? 'block' : 'none'));
	}-*/;

	@Override
	public void onLogout() {
		showBody(false);
	}

}
