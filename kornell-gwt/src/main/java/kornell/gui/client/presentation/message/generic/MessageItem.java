package kornell.gui.client.presentation.message.generic;

import kornell.core.to.ChatThreadMessageTO;

import com.google.gwt.user.client.ui.Label;

public class MessageItem {
	
	private Label label;
	private ChatThreadMessageTO chatThreadMessageTO;
	
	public MessageItem(Label label, ChatThreadMessageTO chatThreadMessageTO){
		this.label = label;
		this.chatThreadMessageTO = chatThreadMessageTO;
	}
	
	public Label getLabel() {
		return label;
	}
	public void setLabel(Label label) {
		this.label = label;
	}
	public ChatThreadMessageTO getChatThreadMessageTO() {
		return chatThreadMessageTO;
	}
	public void setChatThreadMessageTO(ChatThreadMessageTO chatThreadMessageTO) {
		this.chatThreadMessageTO = chatThreadMessageTO;
	}
	
}
