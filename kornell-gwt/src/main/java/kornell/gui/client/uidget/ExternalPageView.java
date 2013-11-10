package kornell.gui.client.uidget;

import java.beans.EventSetDescriptor;
import java.util.Date;

import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.core.lom.ExternalPage;
import kornell.gui.client.event.ViewReadyEvent;
import kornell.gui.client.event.ViewReadyEventHandler;
import kornell.gui.client.presentation.util.loading.LoadingPopup;
import kornell.gui.client.session.UserSession;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class ExternalPageView extends Uidget {
	private IFrameElement iframe;

	private KornellClient client;
	private DateTimeFormat df = DateTimeFormat.getFormat("HH:mm:ss.SSS");

	FlowPanel panel = new FlowPanel();

	private ExternalPage page;

	public ExternalPageView(KornellClient client, ExternalPage page) {
		this.client = client;
		this.page = page;
		createIFrame();
		panel.getElement().appendChild(iframe);
		setSrc(page.getURL());
		initWidget(panel);
	}

	// TODO: fetch these dynamically
	// TODO: Extract view
	static int NORTH_BAR = 45;
	static int SOUTH_BAR = 35;

	private void createIFrame() {
		if (iframe == null) {
			iframe = Document.get().createIFrameElement();
			iframe.addClassName("externalContent");
			iframe.setAttribute("allowtransparency", "true");
			iframe.setAttribute("style", "background-color: black;");
			Event.sinkEvents(iframe, Event.ONLOAD);
			Event.setEventListener(iframe, new EventListener() {

				@Override
				public void onBrowserEvent(Event event) {
					fireViewReady();

				}
			});
		}
		placeIframe();

		// Weird yet simple way of solving FF's weird behavior
		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				Scheduler.get().scheduleDeferred(new Command() {
					@Override
					public void execute() {
						placeIframe();
					}
				});
			}
		});
	}

	private void placeIframe() {
		String width = Window.getClientWidth() + "px";
		iframe.setPropertyString("width", width);
		String height = (Window.getClientHeight() - SOUTH_BAR - NORTH_BAR)
				+ "px";
		iframe.setPropertyString("height", height);
	}

	public void setSrc(final String src) {	
		UserSession.current(new Callback<UserSession>() {
			@Override
			public void ok(UserSession session) {
				// TODO: Check if src exists
				iframe.setSrc(src); 
			}
		});
		
	}

	private String now() {
		return df.format(new Date());
	}

}