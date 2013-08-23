package kornell.gui.client.sequence;

import kornell.gui.client.presentation.atividade.AtividadePlace;


public interface SequencerFactory {

	Sequencer at(AtividadePlace place);

}
