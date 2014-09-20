package kornell.gui.client.event;

import kornell.core.entity.ChatThread;

import com.google.gwt.event.shared.GwtEvent;

public class ComposeMessageEvent extends GwtEvent<ComposeMessageEventHandler>{
	public static final Type<ComposeMessageEventHandler> TYPE = new Type<ComposeMessageEventHandler>();
	
	
	public ComposeMessageEvent() {
	}

	@Override
	protected void dispatch(ComposeMessageEventHandler handler) {
		handler.onComposeMessage(this);		
	}

	@Override
	public Type<ComposeMessageEventHandler> getAssociatedType() {
		return TYPE;
	}
	
}