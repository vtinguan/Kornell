package kornell.gui.client.sequence;

import kornell.api.client.KornellClient;

import com.google.web.bindery.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceHistoryMapper;

public class Stalker implements PlaceChangeEvent.Handler {
	EventBus bus;
	KornellClient client;
	PlaceHistoryMapper mapper;

	public Stalker() {}
	
	public Stalker(EventBus bus, KornellClient client,PlaceHistoryMapper mapper) {
		this.bus = bus;
		this.client = client;
		this.mapper = mapper;
		bus.addHandler(PlaceChangeEvent.TYPE, this);
	}

	@Override
	public void onPlaceChange(PlaceChangeEvent event) {
		String token = mapper.getToken(event.getNewPlace());
		client.placeChanged(token);
	}
}
