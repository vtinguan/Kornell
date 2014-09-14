package kornell.gui.client.presentation.message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.to.ChatThreadMessageTO;
import kornell.core.to.ChatThreadMessagesTO;
import kornell.core.to.UnreadChatThreadTO;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.event.UnreadMessagesPerThreadFetchedEvent;
import kornell.gui.client.event.UnreadMessagesPerThreadFetchedEventHandler;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.presentation.admin.AdminPlace;
import kornell.gui.client.presentation.admin.home.AdminHomePlace;
import kornell.gui.client.presentation.util.KornellNotification;
import kornell.gui.client.presentation.util.LoadingPopup;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class MessagePresenter implements MessageView.Presenter, UnreadMessagesPerThreadFetchedEventHandler{
	private MessageView view;
	private KornellSession session;
	private EventBus bus;
	private PlaceController placeCtrl;
	private boolean isClassPresenter;

	
	private UnreadChatThreadTO selectedChatThreadInfo;
	private Timer chatThreadMessagesTimer;
	
	private List<UnreadChatThreadTO> unreadChatThreadsTOFetchedFromEvent;
	
	private List<UnreadChatThreadTO> unreadChatThreadsTO;
	List<ChatThreadMessageTO> chatThreadMessageTOs;
	private boolean updateMessages = true;
	public MessagePresenter(KornellSession session, EventBus bus, PlaceController placeCtrl, ViewFactory viewFactory) {
		this(session, bus, placeCtrl, viewFactory, false);
	}
	
	public MessagePresenter(KornellSession session, EventBus bus, PlaceController placeCtrl, ViewFactory viewFactory, boolean isClassPresenter) {
		this.session = session;
		this.bus = bus;
		this.placeCtrl = placeCtrl;
		this.isClassPresenter = isClassPresenter;
		view = viewFactory.getMessageView();
		view.setPresenter(this);
		bus.addHandler(UnreadMessagesPerThreadFetchedEvent.TYPE, this);
		init();
		
		bus.addHandler(PlaceChangeEvent.TYPE,
				new PlaceChangeEvent.Handler() {
					@Override
					public void onPlaceChange(PlaceChangeEvent event) {
						selectedChatThreadInfo = null;
						updateMessages = false;
					}
				});
	}
	
	private void init() {
  }
	
	@Override
	public void enableMessagesUpdate(boolean enable){
		this.updateMessages = enable;
	}

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	@Override
  public void onUnreadMessagesPerThreadFetched(UnreadMessagesPerThreadFetchedEvent event) {
		unreadChatThreadsTOFetchedFromEvent = event.getUnreadChatThreadTOs();
		if(placeCtrl.getWhere() instanceof MessagePlace || placeCtrl.getWhere() instanceof AdminPlace)
			filterAndShowThreads();
  }

	@Override
  public void filterAndShowThreads() {
		if(updateMessages){
		  this.unreadChatThreadsTO = filterTOWhenInsideAdminPanel(unreadChatThreadsTOFetchedFromEvent);
			asWidget().setVisible(unreadChatThreadsTO.size() > 0);
		  if(unreadChatThreadsTO.size() == 0 && !isClassPresenter){
		  	KornellNotification.show("Você não tem nenhuma conversa criada.", AlertType.INFO, 5000);
		  } 
		}
  }

	private List<UnreadChatThreadTO> filterTOWhenInsideAdminPanel(List<UnreadChatThreadTO> unreadChatThreadTOs) {
		List<UnreadChatThreadTO> newUnreadChatThreadTOs = new ArrayList<UnreadChatThreadTO>();
	  for (Iterator<UnreadChatThreadTO> iterator = unreadChatThreadTOs.iterator(); iterator.hasNext();) {
	    UnreadChatThreadTO unreadChatThreadTO = (UnreadChatThreadTO) iterator.next();
	    if(!isClassPresenter || Dean.getInstance().getCourseClassTO().getCourseClass().getUUID().equals(unreadChatThreadTO.getCourseClassUUID())){
	    	newUnreadChatThreadTOs.add(unreadChatThreadTO);
	    }
    }
	  if(selectedChatThreadInfo == null && newUnreadChatThreadTOs.size() > 0){
	  	threadClicked(newUnreadChatThreadTOs.get(0));
	  	selectedChatThreadInfo = newUnreadChatThreadTOs.get(0);
	  }
	  if(newUnreadChatThreadTOs.size() > 0){
	  	view.updateSidePanel(newUnreadChatThreadTOs, selectedChatThreadInfo.getChatThreadUUID());
	  }
	  return newUnreadChatThreadTOs;
  }

	@Override
  public void threadClicked(final UnreadChatThreadTO unreadChatThreadTO) {
		LoadingPopup.show();
		initializeChatThreadMessagesTimer();
		this.selectedChatThreadInfo = unreadChatThreadTO;
	  session.chatThreads().getChatThreadMessages(unreadChatThreadTO.getChatThreadUUID(), new Callback<ChatThreadMessagesTO>() {
			@Override
			public void ok(ChatThreadMessagesTO to) {
				chatThreadMessageTOs = to.getChatThreadMessageTOs();
			  view.updateThreadPanel(to, unreadChatThreadTO, session.getCurrentUser().getPerson().getFullName());
				LoadingPopup.hide();
			}
		});
  }

	@Override
  public void sendMessage(String message) {
	  session.chatThreads().postMessageToChatThread(message, selectedChatThreadInfo.getChatThreadUUID(), lastFetchedMessageSentAt(), new Callback<ChatThreadMessagesTO>() {
			@Override
			public void ok(ChatThreadMessagesTO to) {
				chatThreadMessageTOs.addAll(to.getChatThreadMessageTOs());
			  view.addMessagesToThreadPanel(to, session.getCurrentUser().getPerson().getFullName());
			}
		});
  }

	private String lastFetchedMessageSentAt() {
	  return chatThreadMessageTOs.get(chatThreadMessageTOs.size() - 1).getSentAt();
  }
	
	private void initializeChatThreadMessagesTimer() {
		if(chatThreadMessagesTimer != null)
			chatThreadMessagesTimer.cancel();
		
		chatThreadMessagesTimer = new Timer() {
			public void run() {
				getChatThreadMessagesSinceLast();
			}
		};
		// Schedule the timer to run every 20 secs
		chatThreadMessagesTimer.scheduleRepeating(20 * 1000);
	}
	
	private void getChatThreadMessagesSinceLast() {
		final String chatThreadUUID = selectedChatThreadInfo.getChatThreadUUID();
		if((placeCtrl.getWhere() instanceof MessagePlace && !isClassPresenter) || (placeCtrl.getWhere() instanceof AdminHomePlace && isClassPresenter)){
		  session.chatThreads().getChatThreadMessages(chatThreadUUID, lastFetchedMessageSentAt(), new Callback<ChatThreadMessagesTO>() {
				@Override
				public void ok(ChatThreadMessagesTO to) {
					if(selectedChatThreadInfo.getChatThreadUUID().equals(chatThreadUUID)){
						chatThreadMessageTOs.addAll(to.getChatThreadMessageTOs());
					  view.addMessagesToThreadPanel(to, session.getCurrentUser().getPerson().getFullName());
					}
				}
			});
		}
	}


}
