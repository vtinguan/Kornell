package kornell.gui.client.content;

import kornell.api.client.KornellClient;
import kornell.gui.client.presentation.atividade.AtividadePlace;

import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;


public class SequencerFactoryImpl implements SequencerFactory {

	private KornellClient client;
	private EventBus bus;
	private PlaceController ctrl;
	private NamingConventionSequencer sequencer;
	
	public SequencerFactoryImpl(EventBus bus,PlaceController ctrl, KornellClient client) {
		this.client = client;
		this.bus = bus;
		this.ctrl= ctrl;
	}

	@Override
	public Sequencer at(AtividadePlace place) {
		if(sequencer == null)
			sequencer = new NamingConventionSequencer(bus, ctrl, client);
		return sequencer.at(place);  
	}

}
