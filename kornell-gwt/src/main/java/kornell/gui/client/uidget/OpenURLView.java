package kornell.gui.client.uidget;

import java.util.logging.Logger;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;

public class OpenURLView extends Uidget {
	public static final Logger log = Logger.getLogger(OpenURLView.class
			.getName());

	FlowPanel panel = new FlowPanel();
	Frame frame = new Frame(); 
	
	public OpenURLView(String URL) {
		log.fine("Opening URL [" + URL + "][" + System.currentTimeMillis()
				+ "]");
		frame.getElement().addClassName("externalContent");
		frame.getElement().setAttribute("allowFullScreen", "true");
		frame.getElement().setAttribute("webkitallowfullscreen", "true");
		frame.getElement().setAttribute("mozallowfullscreen", "true");

		frame.addLoadHandler(new LoadHandler() {
			@Override
			public void onLoad(LoadEvent event) {
				log.fine("URL loaded [" + System.currentTimeMillis() + "]");
			}
		});

		frame.setUrl(URL);
		panel.add(frame);
		initWidget(panel);
		placeIframe();
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

	// TODO: fetch these dynamically
	// TODO: Extract view
	static int NORTH_BAR = 45;
	static int SOUTH_BAR = 35;
	private void placeIframe() {
		String width = Window.getClientWidth() + "px";
		frame.getElement().setPropertyString("width", width);
		String height = (Window.getClientHeight() - SOUTH_BAR - NORTH_BAR)
				+ "px";
		frame.getElement().setPropertyString("height", height);
	}

}
