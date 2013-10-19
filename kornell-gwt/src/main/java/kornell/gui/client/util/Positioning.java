package kornell.gui.client.util;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;

public class Positioning {

	// TODO: fetch these dynamically
	// TODO: Extract view
	static int NORTH_BAR = 45;
	static int SOUTH_BAR = 35;

	public static void placeBetweenBars(FlowPanel pnl) {
		String width = Window.getClientWidth() + "px";
		String height = (Window.getClientHeight()
				- SOUTH_BAR - NORTH_BAR)
				+ "px";
		pnl.getElement().setPropertyString("width", width);	
		pnl.getElement().setPropertyString("height", height);	
	}

	public static void betweenBars(final FlowPanel pnl) {
		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				Scheduler.get().scheduleDeferred(new Command() {
					@Override
					public void execute() {
						placeBetweenBars(pnl);
					}
				});
			}
		});
		placeBetweenBars(pnl);
	}

}
