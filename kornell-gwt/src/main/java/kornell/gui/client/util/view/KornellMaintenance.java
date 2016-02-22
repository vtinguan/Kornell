package kornell.gui.client.util.view;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;

public class KornellMaintenance {

	public static PopupPanel show() {	
		final PopupPanel popup = new PopupPanel();
		Alert alert = new Alert();
		alert.addStyleName("kornellMessage");
		alert.setType(AlertType.ERROR);
		alert.setText("Estamos em manutenção. Sentimos muito pelo transtorno. Se o problema persistir, entre em contato pelo email suporte@craftware.com.br. Atenciosamente, equipe Eduvem.");
		popup.setWidget(alert);
		
		popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			public void setPosition(int offsetWidth, int offsetHeight) {
				int left = (Window.getClientWidth() - offsetWidth) / 2;
				int top = (Window.getClientHeight() - offsetHeight) / 2;
				popup.setPopupPosition(left, top);
			}
		});
		
		return popup;
	}	
	
	private static native boolean hasPlaceBar() /*-{
		return $wnd.document.getElementsByClassName("placeBar")[0] != null 
			&& $wnd.document.getElementsByClassName("placeBar")[0].getAttribute("aria-hidden") == null;
	}-*/;
}


