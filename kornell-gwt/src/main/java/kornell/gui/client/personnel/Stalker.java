package kornell.gui.client.personnel;

import kornell.api.client.KornellClient;
import kornell.gui.client.presentation.course.CoursePlace;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Tracks user
 * 
 * @author faermanj
 */
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
		Place newPlace = event.getNewPlace();
		if(isRelevant(newPlace)){		
			String token = mapper.getToken(newPlace);
			client.placeChanged(token);
		}
	}

	private boolean isRelevant(Place place) {
		return place instanceof CoursePlace;
	}
}
