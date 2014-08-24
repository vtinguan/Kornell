package kornell.gui.client.personnel;

import kornell.gui.client.event.ComposeMessageEvent;
import kornell.gui.client.event.ComposeMessageEventHandler;
import kornell.gui.client.presentation.message.compose.MessageComposeView;
import kornell.gui.client.util.Positioning;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.web.bindery.event.shared.EventBus;

public class MrPostman implements ComposeMessageEventHandler {

	private static PopupPanel popup;
	private EventBus bus;
	private MessageComposeView.Presenter presenter; 

	public MrPostman(MessageComposeView.Presenter presenter, EventBus bus) {
		this.bus = bus;
		this.presenter = presenter;
		this.bus.addHandler(ComposeMessageEvent.TYPE, this);
	}
	

	@Override
	public void onComposeMessage(ComposeMessageEvent event) {
		if(popup == null || !popup.isShowing()){
			presenter.init(event.getMessage());
			show();
		}
	}
	
	public synchronized void show() {	
		if(popup == null){
			popup = new PopupPanel(false, false);
			popup.addStyleName("messagesPopup");
			FlowPanel panel = new FlowPanel();
			if(presenter != null){
				panel.add(presenter.asWidget());
			}
			popup.setGlassEnabled(false);
			popup.add(panel);
			popup.center();
			popup.setPopupPosition(popup.getAbsoluteLeft(), Positioning.NORTH_BAR);
		}
		popup.show();
	}

	public static void hide() {
		if(popup != null){
			popup.hide();
		}
	}
}
