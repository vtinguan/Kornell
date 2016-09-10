package kornell.gui.client.personnel;

import static kornell.core.util.StringUtils.mkurl;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.Timer;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.KornellSession;
import kornell.core.to.UnreadChatThreadTO;
import kornell.core.util.StringUtils;
import kornell.gui.client.event.LogoutEvent;
import kornell.gui.client.event.LogoutEventHandler;
import kornell.gui.client.event.UnreadMessagesCountChangedEvent;
import kornell.gui.client.event.UnreadMessagesCountChangedEventHandler;
import kornell.gui.client.event.UnreadMessagesPerThreadFetchedEvent;
import kornell.gui.client.event.UnreadMessagesPerThreadFetchedEventHandler;
import kornell.gui.client.util.ClientConstants;
import kornell.gui.client.util.view.KornellMaintenance;

import static kornell.core.util.StringUtils.mkurl;
import static kornell.core.util.StringUtils.isSome;

public class Dean implements LogoutEventHandler, UnreadMessagesPerThreadFetchedEventHandler,
		UnreadMessagesCountChangedEventHandler {

	private String ICON_NAME = "favicon.ico";
	private String DEFAULT_SITE_TITLE = "Kornell";
	private EventBus bus;
	private KornellSession session;
	private int totalCount;

	public Dean(EventBus bus, KornellSession session) {
		this.bus = bus;
		this.session = session;

		bus.addHandler(LogoutEvent.TYPE, this);
		bus.addHandler(UnreadMessagesPerThreadFetchedEvent.TYPE, this);
		bus.addHandler(UnreadMessagesCountChangedEvent.TYPE, this);

		// get the skin and logo immediately
		initInstitutionSkin();
	}

	private void initInstitutionSkin() {
		showContentNative(false);
		
		String url = session.getAssetsURL();
		if (url != null) {
			updateFavicon(mkurl(url, ICON_NAME));
		} else {
			setDefaultFavicon();
		}

		setPageTitle();

		Callback<Void, Exception> callback = new Callback<Void, Exception>() {
			public void onFailure(Exception reason) {
				KornellMaintenance.show();
			}

			public void onSuccess(Void result) {
				showContent(true);
			}
		};

		String skinName = isSome(session.getInstitution().getSkin()) ? session.getInstitution().getSkin() : "";
		String skinPath = mkurl(ClientConstants.CSS_PATH, "skin" + skinName + ".nocache.css");

		JavaScriptObject styleElement = getStyleElement(skinPath);
		attachListeners(styleElement, callback);
		updateSkin(styleElement);
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

	private static native JavaScriptObject getStyleElement(String skinPath) /*-{
		var link = $wnd.document.createElement('link'), oldLink = $wnd.document
				.getElementById('kornellSkin');
		link.id = 'kornellSkin';
		link.rel = 'stylesheet';
		link.type = 'text/css';
		link.href = skinPath;

		return link;
	}-*/;

	private static native void updateSkin(JavaScriptObject styleElement) /*-{
		var oldLink = $wnd.document.getElementById('kornellSkin');
		if (oldLink) {
			$wnd.document.head.removeChild(oldLink);
		}

		// IE8 does not have document.head
		($wnd.document.head || $wnd.document.getElementsByTagName("head")[0])
				.appendChild(styleElement);
	}-*/;

	private static native void attachListeners(JavaScriptObject scriptElement, Callback<Void, Exception> callback) /*-{
	    function clearCallbacks() {
	      scriptElement.onerror = scriptElement.onreadystatechange = scriptElement.onload = null;
	    }
	    scriptElement.onload = $entry(function() {
	      clearCallbacks();
	      if (callback) {
	        callback.@com.google.gwt.core.client.Callback::onSuccess(Ljava/lang/Object;)(null);
	      }
	    });
	    // or possibly more portable script_tag.addEventListener('error', function(){...}, true);
	    scriptElement.onerror = $entry(function() {
	      clearCallbacks();
	      if (callback) {
	        var ex = @com.google.gwt.core.client.CodeDownloadException::new(Ljava/lang/String;)("onerror() called.");
	        callback.@com.google.gwt.core.client.Callback::onFailure(Ljava/lang/Object;)(ex);
	      }
	    });
	    scriptElement.onreadystatechange = $entry(function() {
	      if (/loaded|complete/.test(scriptElement.readyState)) {
	        scriptElement.onload();
	      }
	    });
	  }-*/;

	private void showContent(boolean show) {
		new Timer() {
			public void run() {
				showContentNative(show);
			}
		}.schedule(2000);
	};

	private static native void showContentNative(boolean show) /*-{
		$wnd.document.getElementsByClassName('menuBar')[0].setAttribute('style',
				'display: ' + (show ? 'block' : 'none'));
		$wnd.document.getElementsByClassName('vScrollBar')[0].setAttribute('style',
				'display: ' + (show ? 'block' : 'none'));
		$wnd.document.getElementsByClassName('activityBarWrapper')[0].setAttribute('style',
				'display: ' + (show ? 'block' : 'none'));
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
