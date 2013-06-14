package kornell.gui.client.content;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.web.bindery.event.shared.Event;

public class NavigationRequest extends GwtEvent<NavigationRequest.Handler>{
	public static final Type<NavigationRequest.Handler> TYPE = new Type<NavigationRequest.Handler>();
	private String direction;
	
	private NavigationRequest(String direction){
		this.direction = direction;
	}
	
	public interface Handler extends EventHandler {
	    void onContinue(NavigationRequest event);
	    void onPrevious(NavigationRequest event);
	}

	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		if("continue".equals(direction))
			handler.onContinue(this);
		else if("previous".equals(direction))
			handler.onPrevious(this);
		else
			throw new IllegalStateException("Unknown direction ["+direction+"]");	
	}

	private static final NavigationRequest NEXT = new NavigationRequest("continue");
	private static final NavigationRequest PREV = new NavigationRequest("previous");
	
	public static NavigationRequest next() {		
		return NEXT;
	}
	
	public static NavigationRequest prev() {		
		return PREV;
	}

	public static Event<?> valueOf(String string) {
		return new NavigationRequest(string);
	}
}
