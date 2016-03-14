package kornell.gui.client.personnel;

import kornell.api.client.Callback;
import kornell.api.client.ChatThreadsClient;
import kornell.core.to.UnreadChatThreadsTO;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.event.ComposeMessageEvent;
import kornell.gui.client.event.ComposeMessageEventHandler;
import kornell.gui.client.event.LoginEvent;
import kornell.gui.client.event.LoginEventHandler;
import kornell.gui.client.event.UnreadMessagesFetchedEvent;
import kornell.gui.client.event.UnreadMessagesPerThreadFetchedEvent;
import kornell.gui.client.presentation.admin.AdminPlace;
import kornell.gui.client.presentation.admin.courseclass.courseclass.AdminCourseClassPlace;
import kornell.gui.client.presentation.classroom.ClassroomPlace;
import kornell.gui.client.presentation.message.MessagePlace;
import kornell.gui.client.presentation.message.compose.MessageComposeView;
import kornell.gui.client.presentation.vitrine.VitrinePlace;
import kornell.gui.client.util.view.Positioning;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.web.bindery.event.shared.EventBus;

public class MrPostman implements ComposeMessageEventHandler, LoginEventHandler {

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
		this.bus.addHandler(LoginEvent.TYPE, this);
		
		//initializeUnreadMessagesCountTimer();
		initializeUnreadMessagesCountPerThreadTimer();
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

		// Schedule the timer to run every 30 seconds
		unreadMessagesCountPerThreadTimer.scheduleRepeating(30 * 1000);
		
		bus.addHandler(PlaceChangeEvent.TYPE,
				new PlaceChangeEvent.Handler() {

					@Override 
					public void onPlaceChange(PlaceChangeEvent event) {
						Place place = event.getNewPlace();
						if(place instanceof MessagePlace || place instanceof AdminCourseClassPlace || place instanceof ClassroomPlace){
							getUnreadMessagesPerThread(true);
						}
						if(popup != null && popup.isShowing()){
							boolean showingPlacePanel = !(place instanceof VitrinePlace || place instanceof ClassroomPlace || place instanceof AdminPlace);;
							popup.setPopupPosition(popup.getAbsoluteLeft(), showingPlacePanel ? Positioning.NORTH_BAR_PLUS : Positioning.NORTH_BAR);
						}
					}
				});
  }
	
	private void getUnreadMessagesPerThread() {
		getUnreadMessagesPerThread(false);
	}
	
	private void getUnreadMessagesPerThread(boolean forceFetch) {
		if(forceFetch || !(placeCtrl.getWhere() instanceof VitrinePlace)){
	    chatThreadsClient.getTotalUnreadCountsPerThread(new Callback<UnreadChatThreadsTO>() {
				@Override
				public void ok(UnreadChatThreadsTO unreadChatThreadsTO) {
					bus.fireEvent(new UnreadMessagesPerThreadFetchedEvent(unreadChatThreadsTO.getUnreadChatThreadTOs()));
				}
			});
		}
  }


	@Override
  public void onLogin(UserInfoTO user) {

		// Fetch all messages after 2 seconds
		new Timer() {
			public void run() {
				getUnreadMessagesPerThread(true);
			}
		}.schedule(2 * 1000);

  }


	@Override
	public void onComposeMessage(ComposeMessageEvent event) {
		if(popup == null || !popup.isShowing()){
			presenter.init();
			show(event.isShowingPlacePanel());
		} else {
			hide();
		}
	}
	

	@SuppressWarnings("unused")
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
	    chatThreadsClient.getTotalUnreadCount(new Callback<String>() {
				@Override
				public void ok(String unreadMessagesCount) {
					bus.fireEvent(new UnreadMessagesFetchedEvent(unreadMessagesCount));
				}
			});
		}
  }
	
	public synchronized void show(boolean showingPlacePanel) {	
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
		}
		popup.show();
		popup.setPopupPosition(popup.getAbsoluteLeft(), showingPlacePanel ? Positioning.NORTH_BAR_PLUS : Positioning.NORTH_BAR);
	}

	public static void hide() {
		if(popup != null){
			popup.hide();
		}
	}
}
