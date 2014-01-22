package kornell.gui.client.sequence;

import kornell.api.client.KornellClient;
import kornell.gui.client.presentation.course.CourseClassPlace;
import kornell.scorm.client.scorm12.SCORM12Sequencer;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;

public class SequencerFactoryImpl implements SequencerFactory {

	private KornellClient client;
	private EventBus bus;
	private PlaceController ctrl;
	

	public SequencerFactoryImpl(EventBus bus, PlaceController ctrl,
			KornellClient client) {
		this.client = client;
		this.bus = bus;
		this.ctrl = ctrl;
	}

	@Override
	public Sequencer withPlace(CourseClassPlace place) {
		GWT.log("Creating course sequencer");		
		Sequencer sequencer = null;
		sequencer = new SimpleCourseSequencer(bus,client);
		return sequencer.withPlace(place);
	}

}
