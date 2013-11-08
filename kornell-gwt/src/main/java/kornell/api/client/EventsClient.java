package kornell.api.client;

import java.util.Date;

import kornell.core.event.ActomEntered;
import kornell.core.event.EventFactory;
import kornell.core.lom.Actom;
import kornell.core.util.UUID;

import com.google.gwt.core.client.GWT;

public class EventsClient extends RESTClient {
	// TODO: Move all object creation to client factory
	static final EventFactory factory = GWT.create(EventFactory.class);

	public EventClient actomEntered(String courseUUID, final Actom actom) {
		ActomEntered actomEntered = factory.newActomEntered().as();
		actomEntered.setCourseUUID(courseUUID);
		actomEntered.setActomKey(actom.getKey());		
		actomEntered.setEventFiredAt(new Date());
		actomEntered.setUUID(UUID.random());
		return withEvent(actomEntered);
	}

	private EventClient withEvent(ActomEntered actomEntered) {
		return new EventClient(actomEntered);
	}

}
