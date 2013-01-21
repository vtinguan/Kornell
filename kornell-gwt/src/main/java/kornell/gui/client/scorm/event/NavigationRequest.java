package kornell.gui.client.scorm.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class NavigationRequest extends GwtEvent<NavigationRequest.Handler>{
	public static final Type<NavigationRequest.Handler> TYPE = new Type<NavigationRequest.Handler>();
	private String direction;
	
	public NavigationRequest(String direction){
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
}
