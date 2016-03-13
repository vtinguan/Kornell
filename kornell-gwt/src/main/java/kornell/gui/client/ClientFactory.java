
package kornell.gui.client;

import kornell.api.client.KornellSession;
import kornell.gui.client.mvp.HistoryMapper;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;

public interface ClientFactory {
	
	void startApp();
	
	ViewFactory getViewFactory();
	
	PlaceController getPlaceController();
	HistoryMapper getHistoryMapper();
	EventBus getEventBus();
	Place getDefaultPlace();
	void setDefaultPlace(Place place);
	Place getHomePlace();
	void setHomePlace(Place place);

	void logState();

	KornellSession getKornellSession();



}