package kornell.api.client;

import kornell.core.entity.CourseClassState;
import kornell.core.entity.EnrollmentState;
import kornell.core.event.ActomEntered;
import kornell.core.event.AttendanceSheetSigned;
import kornell.core.event.CourseClassStateChanged;
import kornell.core.event.EnrollmentStateChanged;
import kornell.core.event.EnrollmentTransfered;
import kornell.core.event.Event;
import kornell.core.event.EventFactory;
import kornell.core.util.UUID;

import com.google.gwt.core.client.GWT;

public class EventsClient extends RESTClient {
	// TODO: Move all object creation to client factory
	static final EventFactory factory = GWT.create(EventFactory.class);

	public EventClient actomEntered(String enrollmentUUID, final String actomKey) {
		ActomEntered actomEntered = factory.newActomEntered().as();
		actomEntered.setEnrollmentUUID(enrollmentUUID);
		actomEntered.setActomKey(actomKey);		
		actomEntered.setEventFiredAt(ClientTime.now());
		actomEntered.setUUID(UUID.random());
		return withEvent("/events/actomEntered",ActomEntered.TYPE,actomEntered);
	}

	public EventClient attendanceSheetSigned(String institutionUUID, String personUUID) {
		AttendanceSheetSigned attendanceSheetSigned = factory.newAttendanceSheetSigned().as();
		attendanceSheetSigned.setInstitutionUUID(institutionUUID);
		attendanceSheetSigned.setPersonUUID(personUUID);	
		attendanceSheetSigned.setEventFiredAt(ClientTime.now());
		attendanceSheetSigned.setUUID(UUID.random());
		return withEvent("/events/attendanceSheetSigned",AttendanceSheetSigned.TYPE,attendanceSheetSigned);
	}

	public EventClient enrollmentStateChanged(String enrollmentUUID, String personUUID, EnrollmentState fromState, EnrollmentState toState) {
		EnrollmentStateChanged enrollmentStateChanged = factory.newEnrollmentStateChanged().as();
		enrollmentStateChanged.setEnrollmentUUID(enrollmentUUID);
		enrollmentStateChanged.setEventFiredAt(ClientTime.now());
		enrollmentStateChanged.setFromPersonUUID(personUUID);
		enrollmentStateChanged.setFromState(fromState);
		enrollmentStateChanged.setToState(toState);
		enrollmentStateChanged.setUUID(UUID.random());
		return withEvent("/events/enrollmentStateChanged",EnrollmentStateChanged.TYPE,enrollmentStateChanged);
	}

	public EventClient courseClassStateChanged(String courseClassUUID, String personUUID, CourseClassState fromState, CourseClassState toState) {
		CourseClassStateChanged courseClassStateChanged = factory.newCourseClassStateChanged().as();
		courseClassStateChanged.setCourseClassUUID(courseClassUUID);
		courseClassStateChanged.setEventFiredAt(ClientTime.now());
		courseClassStateChanged.setFromPersonUUID(personUUID);
		courseClassStateChanged.setFromState(fromState);
		courseClassStateChanged.setToState(toState);
		courseClassStateChanged.setUUID(UUID.random());
		return withEvent("/events/courseClassStateChanged",CourseClassStateChanged.TYPE,courseClassStateChanged);
	}

	public EventClient enrollmentTransfered(String enrollmentUUID, String toCourseClassUUID, String fromCourseClassUUID, String personUUID) {
		EnrollmentTransfered enrollmentTransfered = factory.newEnrollmentTransfered().as();
		enrollmentTransfered.setFromPersonUUID(personUUID);
		enrollmentTransfered.setEnrollmentUUID(enrollmentUUID);
		enrollmentTransfered.setFromCourseClassUUID(fromCourseClassUUID);
		enrollmentTransfered.setToCourseClassUUID(toCourseClassUUID);
		enrollmentTransfered.setEventFiredAt(ClientTime.now());
		enrollmentTransfered.setUUID(UUID.random());
		return withEvent("/events/enrollmentTransfered",EnrollmentTransfered.TYPE,enrollmentTransfered);
	}

	private EventClient withEvent(String path, String contentType, Event event) {
		return new EventClient(path,contentType,event);
	}

}
