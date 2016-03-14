package kornell.gui.client.personnel;

import static kornell.core.util.StringUtils.mkurl;
import kornell.api.client.KornellSession;
import kornell.core.to.UnreadChatThreadTO;
import kornell.gui.client.GenericClientFactoryImpl;
import kornell.gui.client.event.LogoutEvent;
import kornell.gui.client.event.LogoutEventHandler;
import kornell.gui.client.event.UnreadMessagesCountChangedEvent;
import kornell.gui.client.event.UnreadMessagesCountChangedEventHandler;
import kornell.gui.client.event.UnreadMessagesPerThreadFetchedEvent;
import kornell.gui.client.event.UnreadMessagesPerThreadFetchedEventHandler;

import com.google.gwt.dom.client.Document;
import com.google.web.bindery.event.shared.EventBus;

public class Dean implements LogoutEventHandler, UnreadMessagesPerThreadFetchedEventHandler,
		UnreadMessagesCountChangedEventHandler {

	private String ICON_NAME = "favicon.ico";
	private String DEFAULT_SITE_TITLE = "Kornell";
	private EventBus bus;
	private KornellSession session;
	private int totalCount;

	public void init() {
		this.bus = GenericClientFactoryImpl.EVENT_BUS;
		this.session = GenericClientFactoryImpl.KORNELL_SESSION;
		
		bus.addHandler(LogoutEvent.TYPE, this);
		bus.addHandler(UnreadMessagesPerThreadFetchedEvent.TYPE, this);
		bus.addHandler(UnreadMessagesCountChangedEvent.TYPE, this);

		// get the skin and logo immediately
		initInstitutionSkin();
		showBody(true);
	}

	private void initInstitutionSkin() {
		String url = session.getAssetsURL();
		if (url != null) {
			updateFavicon(mkurl(url, ICON_NAME));
		} else {
			setDefaultFavicon();
		}

		setPageTitle();
	}

	private void setPageTitle() {
		String name = session.getInstitution().getFullName();
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

	private static native void showBody(boolean show) /*-{
		$wnd.document.getElementsByTagName('body')[0].setAttribute('style',
				'display: ' + (show ? 'block' : 'none'));
	}-*/;

	@Override
	public void onUnreadMessagesPerThreadFetched(UnreadMessagesPerThreadFetchedEvent event) {
		int count = 0;
		for (UnreadChatThreadTO unreadChatThreadTO : event.getUnreadChatThreadTOs()) {
			count = count + Integer.parseInt(unreadChatThreadTO.getUnreadMessages());
		}
		totalCount = count;
		setPageTitle();
	}

	@Override
	public void onUnreadMessagesCountChanged(UnreadMessagesCountChangedEvent event) {
		totalCount = event.isIncrement() ? totalCount + event.getCountChange() : totalCount - event.getCountChange();
		setPageTitle();
	}

	@Override
	public void onLogout() {
		showBody(false);
	}

}
