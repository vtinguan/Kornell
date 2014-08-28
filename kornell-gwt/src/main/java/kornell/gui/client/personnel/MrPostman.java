package kornell.gui.client.personnel;

import kornell.api.client.Callback;
import kornell.api.client.ChatThreadsClient;
import kornell.gui.client.event.ComposeMessageEvent;
import kornell.gui.client.event.ComposeMessageEventHandler;
import kornell.gui.client.event.UnreadMessagesFetchedEvent;
import kornell.gui.client.presentation.message.compose.MessageComposeView;
import kornell.gui.client.util.Positioning;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.web.bindery.event.shared.EventBus;

public class MrPostman implements ComposeMessageEventHandler {

	private static PopupPanel popup;
	private EventBus bus;
	private ChatThreadsClient chatThreadsClient;
	private MessageComposeView.Presenter presenter; 
	private Timer unreadMessagesCountTimer;
	
	public MrPostman(MessageComposeView.Presenter presenter, EventBus bus, ChatThreadsClient chatThreadsClient) {
		this.bus = bus;
		this.presenter = presenter;
		this.chatThreadsClient = chatThreadsClient;
		this.bus.addHandler(ComposeMessageEvent.TYPE, this);
		
		initializeUnreadMessagesCountTimer();
	}
	

	private void initializeUnreadMessagesCountTimer() {
		getUnreadMessages();
		
		unreadMessagesCountTimer = new Timer() {
			public void run() {
				getUnreadMessages();
			}
		};

		// Schedule the timer to run every 3 minutes
		unreadMessagesCountTimer.scheduleRepeating(3 * 60 * 1000);
  }

	private void getUnreadMessages() {
    chatThreadsClient.getTotalUnreadCount(Dean.getInstance().getInstitution().getUUID(), new Callback<String>() {
			@Override
			public void ok(String unreadMessagesCount) {
				bus.fireEvent(new UnreadMessagesFetchedEvent(unreadMessagesCount));
			}
		});
  }


	@Override
	public void onComposeMessage(ComposeMessageEvent event) {
		if(popup == null || !popup.isShowing()){
			presenter.init();
			show();
		} else {
			hide();
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
