package kornell.gui.client.personnel;

import kornell.gui.client.event.ComposeMessageEvent;
import kornell.gui.client.event.ComposeMessageEventHandler;
import kornell.gui.client.presentation.message.compose.MessageComposeView;

import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.Label;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.web.bindery.event.shared.EventBus;

public class MrPostman implements ComposeMessageEventHandler {

	private static PopupPanel popup;
	private EventBus bus;
	private static MessageComposeView.Presenter presenter; 

	public MrPostman(MessageComposeView.Presenter presenter, EventBus bus) {
		this.bus = bus;
		this.presenter = presenter;
		bus.addHandler(ComposeMessageEvent.TYPE, this);
	}

	@Override
	public void onComposeMessage(ComposeMessageEvent event) {
		presenter.init(event.getMessage());
		show();
	}
	
	public synchronized static void show() {	
		if(popup == null){
			popup = new PopupPanel(false, true); // Create a non-modal dialog box that will not auto-hide
			FlowPanel panel = new FlowPanel();
			if(presenter != null){
				panel.add(presenter.asWidget());
			}
			popup.setGlassEnabled(true);
			popup.add(panel);
			popup.center();
			//popup.setPopupPosition(popup.getAbsoluteLeft(), 11);
		}
		popup.show();
	}

	public static void hide() {
		if(popup != null){
			popup.hide();
		}
	}
}
