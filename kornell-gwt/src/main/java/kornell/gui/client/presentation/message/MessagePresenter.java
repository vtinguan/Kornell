package kornell.gui.client.presentation.message;

import java.util.List;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.to.ChatThreadMessagesTO;
import kornell.core.to.UnreadChatThreadTO;
import kornell.gui.client.ClientFactory;
import kornell.gui.client.event.UnreadMessagesPerThreadFetchedEvent;
import kornell.gui.client.event.UnreadMessagesPerThreadFetchedEventHandler;
import kornell.gui.client.presentation.util.KornellNotification;

import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class MessagePresenter implements MessageView.Presenter, UnreadMessagesPerThreadFetchedEventHandler{
	private MessageView view;
	private ClientFactory clientFactory;
	private KornellSession session;
	private EventBus bus;
	
	private UnreadChatThreadTO selectedChatThreadInfo;
	
	private List<UnreadChatThreadTO> unreadChatThreadsTO;

	public MessagePresenter(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		this.session = clientFactory.getKornellSession();
		this.bus = clientFactory.getEventBus();
		
		bus.addHandler(UnreadMessagesPerThreadFetchedEvent.TYPE, this);
		view = clientFactory.getViewFactory().getMessageView();
		view.setPresenter(this);
		init();
	}
	
	private void init() {
  }

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	@Override
  public void onUnreadMessagesPerThreadFetched(UnreadMessagesPerThreadFetchedEvent event) {
	  this.unreadChatThreadsTO = event.getUnreadChatThreadTOs();
	  if(selectedChatThreadInfo == null && unreadChatThreadsTO.size() > 0){
	  	threadClicked(unreadChatThreadsTO.get(0));
	  	selectedChatThreadInfo = unreadChatThreadsTO.get(0);
	  }
	  view.updateSidePanel(unreadChatThreadsTO, selectedChatThreadInfo.getChatThreadUUID());
  }

	@Override
  public void threadClicked(final UnreadChatThreadTO unreadChatThreadTO) {
		this.selectedChatThreadInfo = unreadChatThreadTO;
	  session.chatThreads().getChatThreadMessages(unreadChatThreadTO.getChatThreadUUID(), new Callback<ChatThreadMessagesTO>() {
			@Override
			public void ok(ChatThreadMessagesTO to) {
			  view.updateThreadPanel(to.getChatThreadMessageTOs(), unreadChatThreadTO, session.getCurrentUser().getPerson().getFullName());
			}
		});
  }

	@Override
  public void sendMessage(String message) {
	  session.chatThreads().postMessageToChatThread(message, selectedChatThreadInfo.getChatThreadUUID(), new Callback<Void>() {
			
			@Override
			public void ok(Void to) {
				
			}
		});
  }

}
