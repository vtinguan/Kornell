package kornell.gui.client.presentation.message.generic;

import java.util.ArrayList;
import java.util.List;

import kornell.api.client.Callback;
import kornell.core.to.ChatThreadMessageTO;
import kornell.core.to.UnreadChatThreadTO;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.event.UnreadMessagesFetchedEvent;
import kornell.gui.client.personnel.Dean;
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
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;


public class GenericMessageView extends Composite implements MessageView {

	interface GenericMessageUiBinder extends UiBinder<Widget, GenericMessageView> {
	}

	private static GenericMessageUiBinder uiBinder = GWT.create(GenericMessageUiBinder.class);
	private static FormHelper formHelper = GWT.create(FormHelper.class);
	private static KornellConstants constants = GWT.create(KornellConstants.class);
	private MessageView.Presenter presenter;


	private List<Label> sideItems;

  @UiField FlowPanel sidePanel;
  @UiField FlowPanel threadPanel;
  @UiField ScrollPanel threadPanelItemsScroll;
  @UiField FlowPanel threadPanelItems;
  @UiField Label threadTitle;
  @UiField TextArea messageTextArea;
  @UiField Button btnSend;

	public GenericMessageView() {
		initWidget(uiBinder.createAndBindUi(this));
    ensureDebugId("genericMessageInboxView");
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
  public void updateSidePanel(List<UnreadChatThreadTO> unreadChatThreadsTO, String selectedChatThreadUUID) {
		//sidePanel.clear();
		sideItems = new ArrayList<Label>();
		for (final UnreadChatThreadTO unreadChatThreadTO : unreadChatThreadsTO) {
			String appendCount = !"0".equals(unreadChatThreadTO.getUnreadMessages()) ? " (" + unreadChatThreadTO.getUnreadMessages() + ")" : "";
			final Label label = new Label(unreadChatThreadTO.getChatThreadName() + appendCount);
			label.addStyleName("threadListItem");
			label.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					for (Label lbl : sideItems) {
	          lbl.removeStyleName("selected");
          }
          label.addStyleName("selected");
					presenter.threadClicked(unreadChatThreadTO);
				}
			});
			if(unreadChatThreadTO.getChatThreadUUID().equals(selectedChatThreadUUID)){
        label.addStyleName("selected");
			}
			sidePanel.add(label);
			sideItems.add(label);
    }
  }

	@Override
  public void updateThreadPanel(List<ChatThreadMessageTO> chatThreadMessageTOs, UnreadChatThreadTO unreadChatThreadTO, String currentUserFullName) {
		threadTitle.setText(unreadChatThreadTO.getChatThreadName());
		
		threadPanelItems.clear();
		addMessagesToThreadPanel(chatThreadMessageTOs, currentUserFullName);
    
    messageTextArea.setFocus(true);
    messageTextArea.addKeyUpHandler(new KeyUpHandler() {
        @Override
        public void onKeyUp(KeyUpEvent event) {
          if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER && event.isAnyModifierKeyDown() && event.isControlKeyDown())
            doSend(null);
        }
    });
  }

	@Override
	public void addMessagesToThreadPanel(List<ChatThreadMessageTO> chatThreadMessageTOs, String currentUserFullName) {
	  for (final ChatThreadMessageTO chatThreadMessageTO : chatThreadMessageTOs) {
			FlowPanel threadMessageWrapper = new FlowPanel();
			threadMessageWrapper.addStyleName("threadMessageWrapper");
			
			Label header = new Label(chatThreadMessageTO.getSenderFullName() + " em " + formHelper.getStringFromDate(chatThreadMessageTO.getSentAt()));
			header.addStyleName("threadMessageHeader");
			if(!currentUserFullName.equals(chatThreadMessageTO.getSenderFullName())){
				header.addStyleName("rightText");
			}
			threadMessageWrapper.add(header);
			
			Label item = new Label(chatThreadMessageTO.getMessage());
			item.addStyleName("threadMessageItem");
			threadMessageWrapper.add(item);
			
			threadPanelItems.add(threadMessageWrapper);
    }
		
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
		      @Override
		      public void execute() {
		    		threadPanelItemsScroll.scrollToBottom();
		      }
		});
  }

	@UiHandler("btnSend")
	void doSend(ClickEvent e) {
		if(messageTextArea.getText().length() > 0)
			presenter.sendMessage(messageTextArea.getText());
		messageTextArea.setText("");
	}
}