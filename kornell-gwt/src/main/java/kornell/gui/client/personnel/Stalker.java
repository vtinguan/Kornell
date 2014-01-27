package kornell.gui.client.personnel;

import kornell.api.client.KornellClient;
import kornell.gui.client.event.ActomEnteredEvent;
import kornell.gui.client.event.ActomEnteredEventHandler;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Tracks user
 * 
 * @author faermanj
 */
public class Stalker implements ActomEnteredEventHandler {
	EventBus bus;
	KornellClient client;
	PlaceHistoryMapper mapper;

	
	public Stalker(EventBus bus, KornellClient client) {
		this.bus = bus;
		this.client = client;
		
		bus.addHandler(ActomEnteredEvent.TYPE, this);
	}

	@Override
	public void onActomEntered(ActomEnteredEvent event) {
		client.events().actomEntered(event.getEnrollmentUUID(), event.getActomKey()).fire();		
	}
}
