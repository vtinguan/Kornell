package kornell.gui.client.personnel;

import static kornell.core.util.StringUtils.mkurl;
import kornell.api.client.KornellSession;
import kornell.core.entity.Institution;
import kornell.core.to.CourseClassTO;
import kornell.core.to.UnreadChatThreadTO;
import kornell.gui.client.event.LogoutEvent;
import kornell.gui.client.event.LogoutEventHandler;
import kornell.gui.client.event.UnreadMessagesCountChangedEvent;
import kornell.gui.client.event.UnreadMessagesCountChangedEventHandler;
import kornell.gui.client.event.UnreadMessagesPerThreadFetchedEvent;
import kornell.gui.client.event.UnreadMessagesPerThreadFetchedEventHandler;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.Timer;
import com.google.web.bindery.event.shared.EventBus;

public class Dean implements LogoutEventHandler,
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
		bus.addHandler(LogoutEvent.TYPE, this);
		bus.addHandler(UnreadMessagesPerThreadFetchedEvent.TYPE, this);
		bus.addHandler(UnreadMessagesCountChangedEvent.TYPE, this);

		// get the skin and logo immediately
		// updateSkin(institution.getSkin());
		initInstitutionSkin();

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
		String url = getAssetsURL();
		if (url != null) {
			updateFavicon(mkurl(url, ICON_NAME));
		} else {
			setDefaultFavicon();
		}

		setPageTitle();
	}

	private void setPageTitle() {
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
	
	public String getAssetsURL() {
		return "/repository/" + institution.getAssetsRepositoryUUID();
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
		setPageTitle();
	}

	@Override
	public void onUnreadMessagesCountChanged(
			UnreadMessagesCountChangedEvent event) {
		totalCount = event.isIncrement() ? totalCount + event.getCountChange()
				: totalCount - event.getCountChange();
		setPageTitle();
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
