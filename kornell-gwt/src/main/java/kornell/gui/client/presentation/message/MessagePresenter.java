package kornell.gui.client.presentation.message;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.ChatThreadType;
import kornell.core.to.ChatThreadMessageTO;
import kornell.core.to.ChatThreadMessagesTO;
import kornell.core.to.TOFactory;
import kornell.core.to.UnreadChatThreadTO;
import kornell.core.util.StringUtils;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.event.UnreadMessagesPerThreadFetchedEvent;
import kornell.gui.client.event.UnreadMessagesPerThreadFetchedEventHandler;
import kornell.gui.client.presentation.admin.AdminPlace;
import kornell.gui.client.presentation.admin.courseclass.courseclass.AdminCourseClassPlace;
import kornell.gui.client.presentation.classroom.ClassroomPlace;
import kornell.gui.client.util.view.KornellNotification;
import kornell.gui.client.util.view.LoadingPopup;

public class MessagePresenter implements MessageView.Presenter, UnreadMessagesPerThreadFetchedEventHandler{
	private MessageView view;
	private KornellSession session;
	private PlaceController placeCtrl;
	private ViewFactory viewFactory;
	private MessagePanelType messagePanelType;
	private static TOFactory toFactory = GWT.create(TOFactory.class);
	private KornellConstants constants = GWT.create(KornellConstants.class);

	private UnreadChatThreadTO selectedChatThreadInfo;
	private Timer chatThreadMessagesTimer;

	private List<UnreadChatThreadTO> unreadChatThreadsTOFetchedFromEvent;
	List<ChatThreadMessageTO> chatThreadMessageTOs;
	private boolean updateMessages = true;
	private boolean threadBeginningReached;
	
	public MessagePresenter(KornellSession session, EventBus bus, PlaceController placeCtrl, final ViewFactory viewFactory, final MessagePanelType messagePanelType) {
		this.session = session;
		this.placeCtrl = placeCtrl;
		this.viewFactory = viewFactory;
		this.messagePanelType = messagePanelType;

		bus.addHandler(UnreadMessagesPerThreadFetchedEvent.TYPE, this);
		
		bus.addHandler(PlaceChangeEvent.TYPE,
				new PlaceChangeEvent.Handler() {
			@Override
			public void onPlaceChange(PlaceChangeEvent event) {
				if(event.getNewPlace() instanceof MessagePlace){
					initPlaceBar();
					if(MessagePanelType.inbox.equals(messagePanelType)){
						asWidget().removeStyleName("shy");
					}
				} else {
					if(MessagePanelType.inbox.equals(messagePanelType)){
						asWidget().addStyleName("shy");
					}
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
		
		init();
	}

	private void initPlaceBar() {
		viewFactory.getMenuBarView().initPlaceBar(IconType.COMMENTS, constants.messagesTitle(), constants.messagesDescription());
	}

	private void init() {
		view = viewFactory.getMessageView();
		view.setPresenter(this);
		view.setMessagePanelType(messagePanelType);
		chatThreadMessageTOs = new ArrayList<>();
		if(placeCtrl.getWhere() instanceof MessagePlace){
			initPlaceBar();
		}
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

	@Override
	public void onUnreadMessagesPerThreadFetched(UnreadMessagesPerThreadFetchedEvent event) {
		unreadChatThreadsTOFetchedFromEvent = event.getUnreadChatThreadTOs();
		if(placeCtrl.getWhere() instanceof MessagePlace || placeCtrl.getWhere() instanceof AdminPlace || placeCtrl.getWhere() instanceof ClassroomPlace){
			filterAndShowThreads();
		}
	}

	@Override
	public void filterAndShowThreads() {
		if(updateMessages && unreadChatThreadsTOFetchedFromEvent != null){
			List<UnreadChatThreadTO> newUnreadChatThreadTOs = new ArrayList<UnreadChatThreadTO>();
			for (Iterator<UnreadChatThreadTO> iterator = unreadChatThreadsTOFetchedFromEvent.iterator(); iterator.hasNext();) {
				UnreadChatThreadTO unreadChatThreadTO = (UnreadChatThreadTO) iterator.next();
				if(MessagePanelType.inbox.equals(messagePanelType) || 
						showOnCourseClassSupport(unreadChatThreadTO) ||
						showOnCourseClassGlobal(unreadChatThreadTO) || 
						showOnCourseClassTutor(unreadChatThreadTO)){
					newUnreadChatThreadTOs.add(unreadChatThreadTO);
				}
			}
			
			// create placeholder thread for tutor, since it's only created after the first message
			if(MessagePanelType.courseClassTutor.equals(messagePanelType) && newUnreadChatThreadTOs.size() == 0){
				UnreadChatThreadTO newUnreadChatThreadTO = toFactory.newUnreadChatThreadTO().as();
				newUnreadChatThreadTO.setThreadType(ChatThreadType.TUTORING);
				newUnreadChatThreadTO.setChatThreadCreatorName(session.getCurrentUser().getPerson().getFullName());
				if(session.getCurrentCourseClass() != null){
					newUnreadChatThreadTO.setEntityUUID(session.getCurrentCourseClass().getCourseClass().getUUID());
					newUnreadChatThreadTO.setEntityName(session.getCurrentCourseClass().getCourseClass().getName());
				}
				newUnreadChatThreadTO.setUnreadMessages("0");
				newUnreadChatThreadTO.setChatThreadCreatorName(session.getCurrentUser().getPerson().getFullName());
				newUnreadChatThreadTOs.add(newUnreadChatThreadTO);
				selectedChatThreadInfo = null;
			}
			
			if(newUnreadChatThreadTOs.size() > 0){
				// if no thread is selected, "click" the first one
				if(selectedChatThreadInfo == null){
					threadClicked(newUnreadChatThreadTOs.get(0));
					selectedChatThreadInfo = newUnreadChatThreadTOs.get(0);
				}
				view.updateSidePanel(newUnreadChatThreadTOs, selectedChatThreadInfo.getChatThreadUUID(), session.getCurrentUser().getPerson().getFullName());
			} else if(placeCtrl.getWhere() instanceof MessagePlace && MessagePanelType.inbox.equals(messagePanelType)){
				KornellNotification.show(constants.noThreadsMessage(), AlertType.WARNING, 5000);
			} 

			asWidget().setVisible(newUnreadChatThreadTOs.size() > 0);
		}
	}

	@Override
	public void threadClicked(final UnreadChatThreadTO unreadChatThreadTO) {
		threadBeginningReached = false;
		chatThreadMessageTOs = new ArrayList<>();
		initializeChatThreadMessagesTimer();
		this.selectedChatThreadInfo = unreadChatThreadTO;
		if(unreadChatThreadTO.getChatThreadUUID() != null){
			view.displayThreadPanel(false);
			view.updateThreadPanel(unreadChatThreadTO, session.getCurrentUser().getPerson().getFullName());
			onScrollToTop(true);
		} else {
			ChatThreadMessagesTO chatThreadMessagesTO = toFactory.newChatThreadMessagesTO().as();
			chatThreadMessagesTO.setChatThreadMessageTOs(new ArrayList<ChatThreadMessageTO>());
			view.updateThreadPanel(selectedChatThreadInfo, session.getCurrentUser().getPerson().getFullName());
		}
	}

	private void getChatThreadMessagesSinceLast() {
		if(((placeCtrl.getWhere() instanceof MessagePlace && MessagePanelType.inbox.equals(messagePanelType)) || 
				(placeCtrl.getWhere() instanceof AdminCourseClassPlace && MessagePanelType.courseClassSupport.equals(messagePanelType)) || 
				(placeCtrl.getWhere() instanceof ClassroomPlace && 
						session.getCurrentCourseClass() != null  && 
						( (MessagePanelType.courseClassGlobal.equals(messagePanelType) && session.getCurrentCourseClass().getCourseClass().isCourseClassChatEnabled()) ||
						  (MessagePanelType.courseClassTutor.equals(messagePanelType) && session.getCurrentCourseClass().getCourseClass().isTutorChatEnabled())
						)
				) && selectedChatThreadInfo != null && updateMessages)){
			if(selectedChatThreadInfo != null && selectedChatThreadInfo.getChatThreadUUID() != null){
				final String chatThreadUUID = selectedChatThreadInfo.getChatThreadUUID();
				LoadingPopup.show();
				session.chatThreads().getChatThreadMessages(chatThreadUUID, lastFetchedMessageSentAt(), null, new Callback<ChatThreadMessagesTO>() {
					@Override
					public void ok(ChatThreadMessagesTO to) {
						if(selectedChatThreadInfo.getChatThreadUUID().equals(chatThreadUUID)){
							chatThreadMessageTOs.addAll(0, to.getChatThreadMessageTOs());
							view.addMessagesToThreadPanel(to, session.getCurrentUser().getPerson().getFullName(), false);
						}
						LoadingPopup.hide();
					}
				});
			}
		}
	}

	@Override
	public void onScrollToTop(final boolean scrollToBottomAfterFetchingMessages) {
		if(!threadBeginningReached && selectedChatThreadInfo != null && selectedChatThreadInfo.getChatThreadUUID() != null){
			final String chatThreadUUID = selectedChatThreadInfo.getChatThreadUUID();
			//before
			LoadingPopup.show();
			session.chatThreads().getChatThreadMessages(chatThreadUUID, null, firstFetchedMessageSentAt(), new Callback<ChatThreadMessagesTO>() {
				@Override
				public void ok(ChatThreadMessagesTO to) {
					if(to.getChatThreadMessageTOs().size() == 0){
						threadBeginningReached = true;
						view.addMessagesToThreadPanel(to, session.getCurrentUser().getPerson().getFullName(), true);
						view.setPlaceholder(messagePanelType.equals(MessagePanelType.courseClassTutor) ? constants.tutorPlaceholderMessage() : "");
					} else if(selectedChatThreadInfo != null && selectedChatThreadInfo.getChatThreadUUID().equals(chatThreadUUID)){
						chatThreadMessageTOs.addAll(to.getChatThreadMessageTOs());
						view.addMessagesToThreadPanel(to, session.getCurrentUser().getPerson().getFullName(), true);
					}
					if(scrollToBottomAfterFetchingMessages){
						Timer scrollToBottomTimer = new Timer() {
							@Override
							public void run() {
								view.scrollToBottom();
								view.displayThreadPanel(true);
								LoadingPopup.hide();
							}
						};
						scrollToBottomTimer.schedule(2000);
					} else {
						view.displayThreadPanel(true);
						LoadingPopup.hide();
					}
				}
			});
		}
	}

	@Override
	public void sendMessage(final String message) {
		if(StringUtils.isSome(selectedChatThreadInfo.getChatThreadUUID())){
			LoadingPopup.show();
			session.chatThreads().postMessageToChatThread(message, selectedChatThreadInfo.getChatThreadUUID(), lastFetchedMessageSentAt(), new Callback<ChatThreadMessagesTO>() {
				@Override
				public void ok(ChatThreadMessagesTO to) {
					chatThreadMessageTOs.addAll(to.getChatThreadMessageTOs());
					view.addMessagesToThreadPanel(to, session.getCurrentUser().getPerson().getFullName(), false);
					view.scrollToBottom();
					view.sendSidePanelItemToTop(selectedChatThreadInfo.getChatThreadUUID());
					LoadingPopup.hide();
				}
			});
		} else if(MessagePanelType.courseClassTutor.equals(messagePanelType) && session.getCurrentCourseClass() != null){
			LoadingPopup.show();
			session.chatThreads().postMessageToTutoringCourseClassThread(message, session.getCurrentCourseClass().getCourseClass().getUUID(), new Callback<String>() {
				@Override
				public void ok(String uuid) {
					selectedChatThreadInfo.setChatThreadUUID(uuid);
					threadClicked(selectedChatThreadInfo);
					LoadingPopup.hide();
				}
			});
		}
	}
	
	private boolean isCourseClassThread(String courseClassUUID) {
		return session.getCurrentCourseClass() != null
				&& session.getCurrentCourseClass().getCourseClass().getUUID().equals(courseClassUUID);
	}

	private boolean showOnCourseClassSupport(UnreadChatThreadTO unreadChatThreadTO) {
		return isCourseClassThread(unreadChatThreadTO.getEntityUUID())
				&& MessagePanelType.courseClassSupport.equals(messagePanelType)
				&& ChatThreadType.SUPPORT.equals(unreadChatThreadTO.getThreadType());
	}


	private boolean showOnCourseClassTutor(UnreadChatThreadTO unreadChatThreadTO) {
		return isCourseClassThread(unreadChatThreadTO.getEntityUUID())
				&& (MessagePanelType.courseClassTutor.equals(messagePanelType) || MessagePanelType.courseClassSupport.equals(messagePanelType))
				&& ChatThreadType.TUTORING.equals(unreadChatThreadTO.getThreadType())
				&& unreadChatThreadTO.getChatThreadCreatorName().equals(session.getCurrentUser().getPerson().getFullName());
	}

	private boolean showOnCourseClassGlobal(UnreadChatThreadTO unreadChatThreadTO) {
		return isCourseClassThread(unreadChatThreadTO.getEntityUUID())
				&& MessagePanelType.courseClassGlobal.equals(messagePanelType)
				&& ChatThreadType.COURSE_CLASS.equals(unreadChatThreadTO.getThreadType());
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
	public void clearThreadSelection(){
		this.selectedChatThreadInfo = null;
	}

	private Date lastFetchedMessageSentAt() {
		Date date;
		date = chatThreadMessageTOs.size() > 0 ? chatThreadMessageTOs.get(0).getSentAt() : null;
		return date;
	}

	private Date firstFetchedMessageSentAt() {
		Date date;
		date = chatThreadMessageTOs.size() > 0 ? chatThreadMessageTOs.get(chatThreadMessageTOs.size() - 1).getSentAt()  : null;
		return date;
	}
	
	@Override
	public void scrollToBottom(){
		view.scrollToBottom();
	}

	@Override
	public MessagePanelType getMessagePanelType() {
		return messagePanelType;
	}

}