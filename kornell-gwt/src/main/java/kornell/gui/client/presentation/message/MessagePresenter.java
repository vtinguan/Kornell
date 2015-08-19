package kornell.gui.client.presentation.message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.ChatThreadType;
import kornell.core.to.ChatThreadMessageTO;
import kornell.core.to.ChatThreadMessagesTO;
import kornell.core.to.TOFactory;
import kornell.core.to.UnreadChatThreadTO;
import kornell.core.util.StringUtils;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.event.UnreadMessagesPerThreadFetchedEvent;
import kornell.gui.client.event.UnreadMessagesPerThreadFetchedEventHandler;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.presentation.admin.AdminPlace;
import kornell.gui.client.presentation.admin.courseclass.courseclass.AdminCourseClassPlace;
import kornell.gui.client.presentation.course.ClassroomPlace;
import kornell.gui.client.presentation.util.KornellNotification;
import kornell.gui.client.presentation.util.LoadingPopup;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class MessagePresenter implements MessageView.Presenter, UnreadMessagesPerThreadFetchedEventHandler{
	private MessageView view;
	private KornellSession session;
	private PlaceController placeCtrl;
	private ViewFactory viewFactory;
	private MessagePanelType messagePanelType;
	private static TOFactory toFactory = GWT.create(TOFactory.class);

	private UnreadChatThreadTO selectedChatThreadInfo;
	private Timer chatThreadMessagesTimer;

	private List<UnreadChatThreadTO> unreadChatThreadsTOFetchedFromEvent;

	private List<UnreadChatThreadTO> unreadChatThreadsTO;
	List<ChatThreadMessageTO> chatThreadMessageTOs;
	private boolean updateMessages = true;

	public MessagePresenter(KornellSession session, EventBus bus, PlaceController placeCtrl, final ViewFactory viewFactory, final MessagePanelType messagePanelType) {
		this.session = session;
		this.placeCtrl = placeCtrl;
		this.viewFactory = viewFactory;
		this.messagePanelType = messagePanelType;
		view = viewFactory.getMessageView();
		view.setPresenter(this);
		bus.addHandler(UnreadMessagesPerThreadFetchedEvent.TYPE, this);
		init();
		if(placeCtrl.getWhere() instanceof MessagePlace)
			initPlaceBar();
		bus.addHandler(PlaceChangeEvent.TYPE,
				new PlaceChangeEvent.Handler() {
			@Override
			public void onPlaceChange(PlaceChangeEvent event) {
				if(event.getNewPlace() instanceof MessagePlace){
					initPlaceBar();
				}
				selectedChatThreadInfo = null;
				if(MessagePanelType.courseClassSupport.equals(messagePanelType))
					updateMessages = false;
				else if(!(event.getNewPlace() instanceof MessagePlace) && !(event.getNewPlace() instanceof ClassroomPlace))
					updateMessages = false;
				else
					updateMessages = true;
			}
		});
	}

	private void initPlaceBar() {
		viewFactory.getMenuBarView().initPlaceBar(IconType.ENVELOPE, "Chat", "Acompanhe suas conversas com outros participantes da plataforma");
	}

	private void init() {
	}

	@Override
	public void enableMessagesUpdate(boolean enable){
		this.updateMessages = enable;
		filterAndShowThreads();
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
		if(updateMessages && unreadChatThreadsTOFetchedFromEvent != null && placeCtrl.getWhere() instanceof MessagePlace){
			this.unreadChatThreadsTO = filterTO(unreadChatThreadsTOFetchedFromEvent);
			asWidget().setVisible(unreadChatThreadsTO.size() > 0);
			if(unreadChatThreadsTO.size() == 0 && MessagePanelType.inbox.equals(messagePanelType)){
				KornellNotification.show("Você não tem nenhuma conversa criada.", AlertType.INFO, 5000);
			} 
		}
	}

	private List<UnreadChatThreadTO> filterTO(List<UnreadChatThreadTO> unreadChatThreadTOs) {
		List<UnreadChatThreadTO> newUnreadChatThreadTOs = new ArrayList<UnreadChatThreadTO>();
		for (Iterator<UnreadChatThreadTO> iterator = unreadChatThreadTOs.iterator(); iterator.hasNext();) {
			UnreadChatThreadTO unreadChatThreadTO = (UnreadChatThreadTO) iterator.next();
			if(MessagePanelType.inbox.equals(messagePanelType) || 
					(Dean.getInstance().getCourseClassTO() != null && Dean.getInstance().getCourseClassTO().getCourseClass().getUUID().equals(unreadChatThreadTO.getEntityUUID()) && 
					( (MessagePanelType.courseClassSupport.equals(messagePanelType)  && ChatThreadType.SUPPORT.equals(unreadChatThreadTO.getThreadType())) ||
					  (MessagePanelType.courseClassGlobal.equals(messagePanelType)  && ChatThreadType.COURSE_CLASS.equals(unreadChatThreadTO.getThreadType())) || 
					  (MessagePanelType.courseClassTutor.equals(messagePanelType)  && ChatThreadType.TUTORING.equals(unreadChatThreadTO.getThreadType()))))){
				newUnreadChatThreadTOs.add(unreadChatThreadTO);
			}
		}
		if(MessagePanelType.courseClassTutor.equals(messagePanelType) && newUnreadChatThreadTOs.size() == 0){
			UnreadChatThreadTO newUnreadChatThreadTO = toFactory.newUnreadChatThreadTO().as();
			newUnreadChatThreadTO.setThreadType(ChatThreadType.TUTORING);
			newUnreadChatThreadTO.setChatThreadCreatorName(session.getCurrentUser().getPerson().getFullName());
			if(Dean.getInstance().getCourseClassTO() != null){
				newUnreadChatThreadTO.setEntityUUID(Dean.getInstance().getCourseClassTO().getCourseClass().getUUID());
				newUnreadChatThreadTO.setEntityName(Dean.getInstance().getCourseClassTO().getCourseClass().getName());
			}
			newUnreadChatThreadTO.setUnreadMessages("0");
			newUnreadChatThreadTOs.add(newUnreadChatThreadTO);
		}
		if(selectedChatThreadInfo == null && newUnreadChatThreadTOs.size() > 0){
			threadClicked(newUnreadChatThreadTOs.get(0));
			selectedChatThreadInfo = newUnreadChatThreadTOs.get(0);
		}
		if(newUnreadChatThreadTOs.size() > 0){
			view.updateSidePanel(newUnreadChatThreadTOs, selectedChatThreadInfo.getChatThreadUUID(), session.getCurrentUser().getPerson().getFullName());
		}
		return newUnreadChatThreadTOs;
	}

	@Override
	public void threadClicked(UnreadChatThreadTO unreadChatThreadTO) {
		LoadingPopup.show();
		initializeChatThreadMessagesTimer();
		this.selectedChatThreadInfo = unreadChatThreadTO;
		if(unreadChatThreadTO.getChatThreadUUID() != null){
			session.chatThreads().getChatThreadMessages(unreadChatThreadTO.getChatThreadUUID(), new Callback<ChatThreadMessagesTO>() {
				@Override
				public void ok(ChatThreadMessagesTO to) {
					chatThreadMessageTOs = to.getChatThreadMessageTOs();
					view.updateThreadPanel(to, selectedChatThreadInfo, session.getCurrentUser().getPerson().getFullName());
					LoadingPopup.hide();
				}
			});
		} else {
			ChatThreadMessagesTO chatThreadMessagesTO = toFactory.newChatThreadMessagesTO().as();
			chatThreadMessagesTO.setChatThreadMessageTOs(new ArrayList<ChatThreadMessageTO>());
			view.updateThreadPanel(chatThreadMessagesTO, selectedChatThreadInfo, session.getCurrentUser().getPerson().getFullName());
		}
	}

	@Override
	public void sendMessage(final String message) {
		if(StringUtils.isSome(selectedChatThreadInfo.getChatThreadUUID())){
			session.chatThreads().postMessageToChatThread(message, selectedChatThreadInfo.getChatThreadUUID(), lastFetchedMessageSentAt(), new Callback<ChatThreadMessagesTO>() {
				@Override
				public void ok(ChatThreadMessagesTO to) {
					chatThreadMessageTOs.addAll(to.getChatThreadMessageTOs());
					view.addMessagesToThreadPanel(to, session.getCurrentUser().getPerson().getFullName());
				}
			});
		} else if(MessagePanelType.courseClassTutor.equals(messagePanelType) && Dean.getInstance().getCourseClassTO() != null){
			session.chatThreads().postMessageToTutoringCourseClassThread(message, Dean.getInstance().getCourseClassTO().getCourseClass().getUUID(), new Callback<String>() {
				@Override
				public void ok(String uuid) {
					selectedChatThreadInfo.setChatThreadUUID(uuid);
					/*ChatThreadMessagesTO chatThreadMessagesTO = toFactory.newChatThreadMessagesTO().as();
					ChatThreadMessageTO chatThreadMessageTO = toFactory.newChatThreadMessageTO().as();
					chatThreadMessageTO.setMessage(message);
					chatThreadMessageTO.setSenderFullName(session.getCurrentUser().getPerson().getFullName());
					List<ChatThreadMessageTO> chatThreadMessageTOsNew = new ArrayList<ChatThreadMessageTO>();
					chatThreadMessageTOsNew.add(chatThreadMessageTO);
					chatThreadMessagesTO.setChatThreadMessageTOs(chatThreadMessageTOsNew);
					if(chatThreadMessageTOs == null){
						chatThreadMessageTOs = new ArrayList<ChatThreadMessageTO>();
					}
					chatThreadMessageTOs.addAll(chatThreadMessageTOsNew);
					view.addMessagesToThreadPanel(chatThreadMessagesTO, session.getCurrentUser().getPerson().getFullName());*/
					enableMessagesUpdate(true);
					getChatThreadMessagesSinceLast();
				}
			});
		}
	}

	@Override
	public void clearThreadSelection(){
		this.selectedChatThreadInfo = null;
	}

	private String lastFetchedMessageSentAt() {
		return chatThreadMessageTOs.size() > 0 ? chatThreadMessageTOs.get(chatThreadMessageTOs.size() - 1).getSentAt() : "";
	}

	private void initializeChatThreadMessagesTimer() {
		if(chatThreadMessagesTimer != null)
			chatThreadMessagesTimer.cancel();

		chatThreadMessagesTimer = new Timer() {
			public void run() {
				getChatThreadMessagesSinceLast();
			}
		};
		// Schedule the timer to run every 13 secs
		chatThreadMessagesTimer.scheduleRepeating(13 * 1000);
	}

	private void getChatThreadMessagesSinceLast() {
		if(((placeCtrl.getWhere() instanceof MessagePlace && MessagePanelType.inbox.equals(messagePanelType)) || 
				(placeCtrl.getWhere() instanceof AdminCourseClassPlace && MessagePanelType.courseClassSupport.equals(messagePanelType)) || 
				(placeCtrl.getWhere() instanceof ClassroomPlace && 
						Dean.getInstance().getCourseClassTO() != null  && 
						( (MessagePanelType.courseClassGlobal.equals(messagePanelType) && Dean.getInstance().getCourseClassTO().getCourseClass().isCourseClassChatEnabled()) ||
						  (MessagePanelType.courseClassTutor.equals(messagePanelType) && Dean.getInstance().getCourseClassTO().getCourseClass().isTutorChatEnabled())
						)
				) && selectedChatThreadInfo != null && updateMessages)){
			final String chatThreadUUID = selectedChatThreadInfo.getChatThreadUUID();
			if(chatThreadUUID != null){
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

	@Override
	public MessagePanelType getMessagePanelType() {
		return messagePanelType;
	}

}