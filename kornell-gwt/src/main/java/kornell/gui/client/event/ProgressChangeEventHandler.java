package kornell.gui.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface ProgressChangeEventHandler extends EventHandler{
	
	void onProgressChange(ProgressEvent event);
}
