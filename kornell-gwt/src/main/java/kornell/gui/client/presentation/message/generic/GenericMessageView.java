package kornell.gui.client.presentation.message.generic;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import kornell.api.client.KornellSession;
import kornell.core.to.ChatThreadMessageTO;
import kornell.core.to.ChatThreadMessagesTO;
import kornell.core.to.UnreadChatThreadTO;
import kornell.core.util.StringUtils;
import kornell.gui.client.event.UnreadMessagesCountChangedEvent;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.presentation.message.MessagePanelType;
import kornell.gui.client.presentation.message.MessageView;
import kornell.gui.client.presentation.util.FormHelper;

import com.github.gwtbootstrap.client.ui.TextArea;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;


public class GenericMessageView extends Composite implements MessageView {

	interface GenericMessageUiBinder extends UiBinder<Widget, GenericMessageView> {
	}

	private static GenericMessageUiBinder uiBinder = GWT.create(GenericMessageUiBinder.class);
	private static FormHelper formHelper = GWT.create(FormHelper.class);
	private MessageView.Presenter presenter;
	private EventBus bus;
	private KornellSession session;
	private MessagePanelType messagePanelType;


	private List<Label> sideItems;
	Map<String, MessageItem> dateLabelsMap;
	ScrollHandler scrollHandler;
	HandlerRegistration handlerRegistration;
	

	@UiField FlowPanel sidePanel;
	@UiField FlowPanel threadPanel;
	@UiField ScrollPanel threadPanelItemsScroll;
	@UiField FlowPanel threadPanelItems;
	@UiField Label threadTitle;
	@UiField TextArea messageTextArea;
	@UiField Button btnSend;
	
	String INFO_CLASS = "textInfoColor";
	String HIGHLIGHT_CLASS = "highlightTextDiscreteColor";
	String PLAIN_CLASS = "plainDiscreteTextColor";

	public GenericMessageView(EventBus eventBus, KornellSession session) {
		this.bus = eventBus;
		this.session = session;
		initWidget(uiBinder.createAndBindUi(this));
		ensureDebugId("genericMessageInboxView");

		dateLabelsMap = new HashMap<String, MessageItem>();
		
		if(handlerRegistration != null){
			handlerRegistration.removeHandler(); 
		}
		
		scrollHandler = new ScrollHandler() {
			@Override
			public void onScroll(ScrollEvent event) {
				if(threadPanelItemsScroll.getVerticalScrollPosition() == 0){
					presenter.onScrollToTop(false);
				}
			}
		};
		handlerRegistration = threadPanelItemsScroll.addScrollHandler(scrollHandler);
	}

	@Override
	protected void onEnsureDebugId(String baseID) {
		sidePanel.ensureDebugId(baseID + "-sidePanel");
		threadPanel.ensureDebugId(baseID + "-threadPanel");
	}

	@Override
	public void setPresenter(Presenter p) {
		presenter = p;
	}

	@Override
	public void updateSidePanel(List<UnreadChatThreadTO> unreadChatThreadsTO, String selectedChatThreadUUID, final String currentUserFullName) {
		sidePanel.clear();
		sideItems = new ArrayList<Label>();
		for (final UnreadChatThreadTO unreadChatThreadTO : unreadChatThreadsTO) {
			final Label label = new Label();
			label.addStyleName("threadListItem");
			label.addClickHandler(new ClickHandler() {
				boolean enableClick = true;
				@Override
				public void onClick(ClickEvent event) {
					if(!enableClick) return;
					enableClick = false;
					Timer preventDoubleClickTimer = new Timer() {
						public void run() {
							enableClick = true;
						}
					};
					preventDoubleClickTimer.schedule(300);

					for (Label lbl : sideItems) {
						lbl.removeStyleName("selected");
					}
					label.addStyleName("selected");
					presenter.threadClicked(unreadChatThreadTO);
					setLabelContent(unreadChatThreadTO, label, true, currentUserFullName);
				}
			});
			if(unreadChatThreadTO.getChatThreadUUID().equals(selectedChatThreadUUID)){
				label.addStyleName("selected");
				setLabelContent(unreadChatThreadTO, label, true, currentUserFullName);
			} else {
				setLabelContent(unreadChatThreadTO, label, false, currentUserFullName);
			}
			sidePanel.add(label);
			sideItems.add(label);
		}
	}
	
	private String getThreadTitle(final UnreadChatThreadTO unreadChatThreadTO, String currentUserFullName, boolean lineBreak) {
		switch (unreadChatThreadTO.getThreadType()) {
			case COURSE_CLASS:
				return span("Chat global da turma:", PLAIN_CLASS) + separator(lineBreak) + span(unreadChatThreadTO.getEntityName(), INFO_CLASS);
			case DIRECT:
				return span("Chat direto com:", PLAIN_CLASS) + separator(lineBreak) + span(unreadChatThreadTO.getEntityName(), INFO_CLASS);
			case SUPPORT:
				if (unreadChatThreadTO.getChatThreadCreatorName().equals(currentUserFullName) && !session.isCourseClassAdmin(unreadChatThreadTO.getEntityUUID())) {
					return span("Ajuda para turma:", PLAIN_CLASS) + separator(lineBreak) + span(unreadChatThreadTO.getEntityName(), INFO_CLASS);
				} else {
					return span(unreadChatThreadTO.getChatThreadCreatorName(), HIGHLIGHT_CLASS) + (MessagePanelType.courseClassSupport.equals(presenter.getMessagePanelType()) ? separator(lineBreak, true) + span("Ajuda", PLAIN_CLASS) : separator(lineBreak, true) + span("Ajuda para turma:", PLAIN_CLASS) + separator(lineBreak) + span(unreadChatThreadTO.getEntityName(), INFO_CLASS));
				}
			case TUTORING:
				if (unreadChatThreadTO.getChatThreadCreatorName().equals(currentUserFullName) && !session.isCourseClassTutor(unreadChatThreadTO.getEntityUUID())) {
					return span("Tutoria para turma:", PLAIN_CLASS) + separator(lineBreak) + span(unreadChatThreadTO.getEntityName(), INFO_CLASS);
				} else {
					return span(unreadChatThreadTO.getChatThreadCreatorName(), HIGHLIGHT_CLASS) + (MessagePanelType.courseClassSupport.equals(presenter.getMessagePanelType()) || MessagePanelType.courseClassTutor.equals(presenter.getMessagePanelType()) ? separator(lineBreak, true) + span("Tutoria", PLAIN_CLASS) : separator(lineBreak, true) + span("Tutoria para turma:", PLAIN_CLASS) + separator(lineBreak) + span(unreadChatThreadTO.getEntityName(), INFO_CLASS));
				}
			case INSTITUTION_SUPPORT:
				return span("Ajuda de instituição para turma:", PLAIN_CLASS) + separator(lineBreak) + span(unreadChatThreadTO.getEntityName(), INFO_CLASS);
			case PLATFORM_SUPPORT:
				return span("Ajuda para instituição:", PLAIN_CLASS) + separator(lineBreak) + span(Dean.getInstance().getInstitution().getName(), INFO_CLASS);
			default:
				return  "";
			}
	}

	private String separator(boolean lineBreak) {
		return separator(lineBreak, false);
	}

	private String separator(boolean lineBreak, boolean dash) {
		return lineBreak ? "<br>" : (dash ? "&nbsp;&nbsp;-&nbsp;&nbsp;" : "&nbsp;&nbsp;&nbsp;");
	}

	private String span(String str, String className) {
		return "<span class=\""+className+"\">"+str+"</span>";
	}

	private void setLabelContent(final UnreadChatThreadTO unreadChatThreadTO, final Label label, boolean markAsRead, String currentUserFullName) {
		String appendCount = !"0".equals(unreadChatThreadTO.getUnreadMessages()) && !markAsRead ? " (" + unreadChatThreadTO.getUnreadMessages() + ")&nbsp;&nbsp;" : "";
		appendCount = "<span class=\"unreadCount\">" + appendCount + "</span>";
		
		label.getElement().setInnerHTML(appendCount + getThreadTitle(unreadChatThreadTO, currentUserFullName, true));
		//if it's supposed to be marked as read and there were messages on the thread, update the envelope count
		if(markAsRead && !"0".equals(unreadChatThreadTO.getUnreadMessages())){
			bus.fireEvent(new UnreadMessagesCountChangedEvent(Integer.parseInt(unreadChatThreadTO.getUnreadMessages())));
			unreadChatThreadTO.setUnreadMessages("0");
		}
	}

	@Override
	public void updateThreadPanel(UnreadChatThreadTO unreadChatThreadTO, String currentUserFullName) {
		handlerRegistration.removeHandler();
		threadTitle.getElement().setInnerHTML(getThreadTitle(unreadChatThreadTO, currentUserFullName, false));
		synchronized(dateLabelsMap){
			dateLabelsMap = new HashMap<String, MessageItem>();
		}
		threadPanelItems.clear();
		prepareTextArea(false);
		messageTextArea.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER && event.isAnyModifierKeyDown() && event.isControlKeyDown())
					doSend(null);
			}
		});
		
	}

	@Override
	public void scrollToBottom() {
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				threadPanelItemsScroll.scrollToBottom();
			}
		});
	}
	
	@Override
	public void setPlaceholder(String placeholder){
		messageTextArea.setPlaceholder(placeholder);
	}

	@Override
	public void addMessagesToThreadPanel(ChatThreadMessagesTO chatThreadMessagesTO, String currentUserFullName, final boolean insertOnTop) {
		final int oldPosition = threadPanelItemsScroll.getMaximumVerticalScrollPosition();
		boolean shouldScrollToBottom = (threadPanelItemsScroll.getMaximumVerticalScrollPosition()  == threadPanelItemsScroll.getVerticalScrollPosition()) && !insertOnTop;
		synchronized(dateLabelsMap){
			for (final ChatThreadMessageTO chatThreadMessageTO : chatThreadMessagesTO.getChatThreadMessageTOs()) {
				FlowPanel threadMessageWrapper = new FlowPanel();
				threadMessageWrapper.addStyleName("threadMessageWrapper");
				Label header = new Label("");
	
				header.addStyleName("threadMessageHeader");
				if(currentUserFullName.equals(chatThreadMessageTO.getSenderFullName())){
					header.addStyleName("rightText");
					threadMessageWrapper.addStyleName("overrideWrapper");
				}
				threadMessageWrapper.add(header);
	
				Label item = new Label(chatThreadMessageTO.getMessage());
				item.addStyleName("threadMessageItem");
				threadMessageWrapper.add(item);
				
				if(!dateLabelsMap.containsKey(chatThreadMessageTO.getSentAt())){
					dateLabelsMap.put(chatThreadMessageTO.getSentAt(), new MessageItem(header, chatThreadMessageTO));
					if(insertOnTop){
						threadPanelItems.insert(threadMessageWrapper, 0);
					} else {
						threadPanelItems.add(threadMessageWrapper);
					}
				}
			} 
		}
		
		updateDateLabelValues(chatThreadMessagesTO.getServerTime());
		
		if(insertOnTop){
			Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
				@Override
				public void execute() {
					threadPanelItemsScroll.setVerticalScrollPosition(threadPanelItemsScroll.getMaximumVerticalScrollPosition() - oldPosition);
					if(threadPanelItemsScroll.getVerticalScrollPosition() == 0){
						handlerRegistration.removeHandler();
						presenter.onScrollToTop(oldPosition > 0);
					} else {
						handlerRegistration.removeHandler();
						handlerRegistration = threadPanelItemsScroll.addScrollHandler(scrollHandler);
					}
				}
			});
		}
		if(shouldScrollToBottom){
			scrollToBottom();
		}
	}

	private void updateDateLabelValues(String serverTime) {
		synchronized(dateLabelsMap){
			Iterator<Entry<String, MessageItem>> it = dateLabelsMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, MessageItem> pairs = (Map.Entry<String, MessageItem>)it.next();
				pairs.getValue().getLabel().getElement().setInnerHTML(getDateLabelValue(serverTime, pairs.getValue().getChatThreadMessageTO()));
			}
		}
	}

	private String getDateLabelValue(String serverTimeStr, final ChatThreadMessageTO chatThreadMessageTO) {
		if(StringUtils.isNone(chatThreadMessageTO.getSentAt())) return "";
		Date sentAt = formHelper.getJudFromString(chatThreadMessageTO.getSentAt());
		Date serverTime = formHelper.getJudFromString(serverTimeStr);
		String dateStr = span(chatThreadMessageTO.getSenderFullName(), INFO_CLASS) + separator(false, false) + span(formHelper.getElapsedTimeSince(sentAt, serverTime), PLAIN_CLASS);
		return dateStr;
	}

	@UiHandler("btnSend")
	void doSend(ClickEvent e) {
		if(messageTextArea.getText().trim().length() > 0){
			presenter.sendMessage(messageTextArea.getText());
			prepareTextArea(true);
		}
	}

	private void prepareTextArea(boolean setFocus) {
		messageTextArea.setText("");
		if(setFocus){
		  Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			      @Override
			      public void execute() {
			        messageTextArea.setFocus(true);
			      }
			});
		}
	}
	
	@Override
	public void setMessagePanelType(MessagePanelType messagePanelType) {
		this.messagePanelType = messagePanelType;
	}
}