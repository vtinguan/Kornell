package kornell.gui.client.sequence;

import java.util.logging.Logger;

import kornell.api.client.KornellSession;
import kornell.api.client.RESTClient;
import kornell.gui.client.presentation.course.ClassroomPlace;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;

public class SequencerFactoryImpl implements SequencerFactory {

	Logger logger = Logger.getLogger(SequencerFactoryImpl.class.getName());
	private KornellSession session;
	private EventBus bus;

	public SequencerFactoryImpl(EventBus bus, PlaceController ctrl,
			KornellSession session) {
		this.session = session;
		this.bus = bus;
	}	

	@Override
	public Sequencer withPlace(ClassroomPlace place) {
		logger.info("Creating course sequencer");
		Sequencer sequencer = null;
		sequencer = new ThinSequencer(bus,session); //new PrefetchSequencer(bus, session);		
		return sequencer.withPlace(place);
	}

}