package kornell.gui.client.util.view;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;

import kornell.core.util.StringUtils;

public class KornellNotification {

	public static void show(String message) {
		show(message, AlertType.SUCCESS);
	}
	
	/*
	 * Helper method for JSNI call
	 */
	public static void showError(String message) {
		show(message, AlertType.ERROR, 2500);
	}

	public static void show(String message, AlertType alertType) {
		show(message, alertType, 2500);
	}

	public static void show(String message, int timer) {
		show(message, AlertType.SUCCESS, timer);
	}

	public static PopupPanel show(String message, AlertType alertType, int timer) {	
		if(StringUtils.isNone(message)) return null;
		final PopupPanel popup = new PopupPanel();
		Alert alert = new Alert();
		alert.addStyleName("kornellMessage");
		alert.setType(alertType);
		alert.setText(message);
		popup.setWidget(alert);
		
		popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			public void setPosition(int offsetWidth, int offsetHeight) {
				int left = (Window.getClientWidth() - offsetWidth) / 2;
				popup.setPopupPosition(left, Positioning.NORTH_BAR_PLUS);
			}
		});
		
		if(timer > 0){
			new Timer() {
				@Override
				public void run() {
					popup.hide();
					this.cancel();
				}
			}.scheduleRepeating(timer);
		}
		
		return popup;
	}	
	
	private static native boolean hasPlaceBar() /*-{
		return $wnd.document.getElementsByClassName("placeBar")[0] != null 
			&& $wnd.document.getElementsByClassName("placeBar")[0].getAttribute("aria-hidden") == null;
	}-*/;
}


