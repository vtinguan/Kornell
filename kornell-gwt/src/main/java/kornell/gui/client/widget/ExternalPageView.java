package kornell.gui.client.widget;

import kornell.api.client.Callback;
import kornell.api.client.KornellClient;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class ExternalPageView extends Composite {
	private IFrameElement iframe;
	private KornellClient client;

	public ExternalPageView(KornellClient client) {
		this.client = client;
		createIFrame();
		FlowPanel panel = new FlowPanel();
		panel.getElement().appendChild(iframe);
		initWidget(panel);
	}

	// TODO: fetch these dynamically
	// TODO: Extract view
	int NORTH_BAR = 45;
	int SOUTH_BAR = 35;

	private void createIFrame() {
		if (iframe == null) {
			iframe = Document.get().createIFrameElement();
			iframe.addClassName("externalContent");
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
		iframe.setPropertyString("width", Window.getClientWidth() + "px");
		iframe.setPropertyString("height", (Window.getClientHeight()
				- SOUTH_BAR - NORTH_BAR)
				+ "px");
	}

	public void setSrc(final String src) {
		iframe.setSrc(src);
		//TODO: Validate before redirect
		/*
		client.check(src, new Callback(){
			@Override
			protected void ok() {
				GWT.log("URL ["+src+"] IS OK");
			}
		});
		*/		
	}
}
