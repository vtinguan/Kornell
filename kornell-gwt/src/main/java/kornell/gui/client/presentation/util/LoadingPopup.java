package kornell.gui.client.presentation.util;

import kornell.gui.client.KornellConstants;

import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.Label;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;

public class LoadingPopup {
	private static PopupPanel popup;
	private static KornellConstants constants = GWT.create(KornellConstants.class);
	
	
	public static void show() {
		//TODO: i18n
		show(null);
	}
	
	public synchronized static void show(String message) {	
		if(popup == null){
			popup = new PopupPanel(false, false); // Create a non-modal dialog box that will not auto-hide
			popup.addStyleName("loadingPopup");
			FlowPanel panel = new FlowPanel();
			panel.addStyleName("ajaxLoaderPanel");
			if(message != null){
				panel.add(new Label(" " + message + " "));
			}
			panel.add(new Image("skins/first/ajax-loader.gif"));
			//popup.setGlassEnabled(true);
			popup.add(panel);
			popup.center();
			popup.setPopupPosition(popup.getAbsoluteLeft(), 11);
		}
		popup.show();
	}

	public static void hide() {
		if(popup != null){
			popup.hide();
		}
	}
}


