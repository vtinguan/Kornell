package kornell.api.client;

import java.util.Date;

import com.google.gwt.core.client.GWT;

import kornell.core.event.ActomEntered;
import kornell.core.event.EventFactory;
import kornell.core.lom.Actom;
import kornell.core.to.UserInfoTO;

public class EventsClient extends RESTClient {
	// TODO: Move all object creation to client factory
	static final EventFactory factory = GWT.create(EventFactory.class);

	public EventClient actomEntered(final Actom actom) {
		ActomEntered actomEntered = factory.newActomEntered().as();
		actomEntered.setActomKey(actom.getKey());		
		actomEntered.setTime(new Date());
		return withEvent(actomEntered);
	}

	private EventClient withEvent(ActomEntered actomEntered) {
		return new EventClient(actomEntered);
	}

}
