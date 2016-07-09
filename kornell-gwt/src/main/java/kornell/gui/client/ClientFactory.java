
package kornell.gui.client;

import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.KornellSession;
import kornell.gui.client.mvp.HistoryMapper;

public interface ClientFactory {
	
	void startApp();
	
	ViewFactory getViewFactory();
	
	PlaceController getPlaceController();
	HistoryMapper getHistoryMapper();
	EventBus getEventBus();

	void logState();

	KornellSession getKornellSession();




}