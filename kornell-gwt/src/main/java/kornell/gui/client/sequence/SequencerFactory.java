package kornell.gui.client.sequence;

import kornell.gui.client.presentation.course.ClassroomPlace;


public interface SequencerFactory {

	Sequencer withPlace(ClassroomPlace place);

}
