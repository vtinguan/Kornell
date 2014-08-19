package kornell.gui.client.event;

import kornell.core.entity.Message;

import com.google.gwt.event.shared.GwtEvent;

public class ComposeMessageEvent extends GwtEvent<ComposeMessageEventHandler>{
	public static final Type<ComposeMessageEventHandler> TYPE = new Type<ComposeMessageEventHandler>();
	
	private Message message;
	
	public ComposeMessageEvent(Message message) {
		this.message = message;
	}

	@Override
	protected void dispatch(ComposeMessageEventHandler handler) {
		handler.onComposeMessage(this);		
	}

	@Override
	public Type<ComposeMessageEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	public Message getMessage() {
		return message;
	}
	
}