package kornell.gui.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface NavigationForecastEventHandler  extends EventHandler{
	
	void onNextActivityNotOK(NavigationForecastEvent evt);
	void onNextActivityOK(NavigationForecastEvent evt);
}
