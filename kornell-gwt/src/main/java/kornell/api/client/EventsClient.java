package kornell.api.client;

import java.util.Date;

import kornell.core.entity.EnrollmentState;
import kornell.core.event.ActomEntered;
import kornell.core.event.EnrollmentStateChanged;
import kornell.core.event.Event;
import kornell.core.event.EventFactory;
import kornell.core.lom.Actom;
import kornell.core.util.UUID;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;

public class EventsClient extends RESTClient {
	// TODO: Move all object creation to client factory
	static final EventFactory factory = GWT.create(EventFactory.class);

	public EventClient actomEntered(String personUUID,String courseUUID, final String actomKey) {
		ActomEntered actomEntered = factory.newActomEntered().as();
		actomEntered.setCourseUUID(courseUUID);
		actomEntered.setActomKey(actomKey);		
		actomEntered.setEventFiredAt(new Date());
		actomEntered.setUUID(UUID.random());
		actomEntered.setFromPersonUUID(personUUID);
		return withEvent("/events/actomEntered",ActomEntered.TYPE,actomEntered);
	}

	public EventClient enrollmentStateChanged(String enrollmentUUID, String personUUID, EnrollmentState fromState, EnrollmentState toState) {
		EnrollmentStateChanged enrollmentStateChanged = factory.newEnrollmentStateChanged().as();
		enrollmentStateChanged.setEnrollmentUUID(enrollmentUUID);
		enrollmentStateChanged.setEventFiredAt(new Date());
		enrollmentStateChanged.setFromPersonUUID(personUUID);
		enrollmentStateChanged.setFromState(fromState);
		enrollmentStateChanged.setToState(toState);
		enrollmentStateChanged.setUUID(UUID.random());
		return withEvent("/events/enrollmentStateChanged",EnrollmentStateChanged.TYPE,enrollmentStateChanged);
	}

	private EventClient withEvent(String path, String contentType, Event event) {
		return new EventClient(path,contentType,event);
	}

}
