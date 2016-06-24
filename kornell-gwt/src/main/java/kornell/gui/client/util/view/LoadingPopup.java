package kornell.gui.client.util.view;

import static kornell.core.util.StringUtils.mkurl;

import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.Label;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;

import kornell.gui.client.util.ClientConstants;

public class LoadingPopup {
	private static PopupPanel popup;	
	
	public static void show() {
		show(null);
	}
	
	public static void show(String message) {	
		if(popup == null){
			popup = new PopupPanel(false, false); // Create a non-modal dialog box that will not auto-hide
			popup.addStyleName("loadingPopup");
			FlowPanel panel = new FlowPanel();
			panel.addStyleName("ajaxLoaderPanel");
			if(message != null){
				panel.add(new Label(" " + message + " "));
			}
			panel.add(new Image(mkurl(ClientConstants.IMAGES_PATH, "ajax-loader.gif")));
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


