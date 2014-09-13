package kornell.gui.client.personnel;

import kornell.api.client.Callback;
import kornell.api.client.ChatThreadsClient;
import kornell.core.to.UnreadChatThreadsTO;
import kornell.gui.client.event.ComposeMessageEvent;
import kornell.gui.client.event.ComposeMessageEventHandler;
import kornell.gui.client.event.UnreadMessagesFetchedEvent;
import kornell.gui.client.event.UnreadMessagesPerThreadFetchedEvent;
import kornell.gui.client.presentation.admin.home.AdminHomePlace;
import kornell.gui.client.presentation.message.MessagePlace;
import kornell.gui.client.presentation.message.compose.MessageComposeView;
import kornell.gui.client.presentation.vitrine.VitrinePlace;
import kornell.gui.client.util.Positioning;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.web.bindery.event.shared.EventBus;

public class MrPostman implements ComposeMessageEventHandler {

	private static PopupPanel popup;
	private EventBus bus;
	private ChatThreadsClient chatThreadsClient;
	private PlaceController placeCtrl;
	private MessageComposeView.Presenter presenter; 
	private Timer unreadMessagesCountTimer;
	private Timer unreadMessagesCountPerThreadTimer;
	
	public MrPostman(MessageComposeView.Presenter presenter, EventBus bus, ChatThreadsClient chatThreadsClient, PlaceController placeCtrl) {
		this.bus = bus;
		this.presenter = presenter;
		this.chatThreadsClient = chatThreadsClient;
		this.placeCtrl = placeCtrl;
		this.bus.addHandler(ComposeMessageEvent.TYPE, this);
		
		initializeUnreadMessagesCountTimer();
		
		initializeUnreadMessagesCountPerThreadTimer();
	}
	

	private void initializeUnreadMessagesCountTimer() {

		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
		      @Override
		      public void execute() {
		    		getUnreadMessages();
		      }
		});
		
		unreadMessagesCountTimer = new Timer() {
			public void run() {
				getUnreadMessages();
			}
		};

		// Schedule the timer to run every 2 minutes
		unreadMessagesCountTimer.scheduleRepeating(2 * 60 * 1000);
  }
	private void getUnreadMessages() {
		if(!(placeCtrl.getWhere() instanceof VitrinePlace)){
	    chatThreadsClient.getTotalUnreadCount(Dean.getInstance().getInstitution().getUUID(), new Callback<String>() {
				@Override
				public void ok(String unreadMessagesCount) {
					bus.fireEvent(new UnreadMessagesFetchedEvent(unreadMessagesCount));
				}
			});
		}
  }

	private void initializeUnreadMessagesCountPerThreadTimer() {

		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
		      @Override
		      public void execute() {
		    		getUnreadMessagesPerThread();
		      }
		});
		
		unreadMessagesCountPerThreadTimer = new Timer() {
			public void run() {
				getUnreadMessagesPerThread();
			}
		};

		// Schedule the timer to run every 30 secs
		unreadMessagesCountPerThreadTimer.scheduleRepeating(30 * 1000);
		
		bus.addHandler(PlaceChangeEvent.TYPE,
				new PlaceChangeEvent.Handler() {
					@Override
					public void onPlaceChange(PlaceChangeEvent event) {
						getUnreadMessagesPerThread();
					}
				});
  }
	private void getUnreadMessagesPerThread() {
		if(placeCtrl.getWhere() instanceof MessagePlace || placeCtrl.getWhere() instanceof AdminHomePlace){
	    chatThreadsClient.getTotalUnreadCountsPerThread(Dean.getInstance().getInstitution().getUUID(), new Callback<UnreadChatThreadsTO>() {
				@Override
				public void ok(UnreadChatThreadsTO unreadChatThreadsTO) {
					bus.fireEvent(new UnreadMessagesPerThreadFetchedEvent(unreadChatThreadsTO.getUnreadChatThreadTOs()));
				}
			});
		}
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
