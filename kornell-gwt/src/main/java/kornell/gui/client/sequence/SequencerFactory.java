package kornell.gui.client.sequence;

import kornell.gui.client.presentation.course.CourseClassPlace;


public interface SequencerFactory {

	Sequencer withPlace(CourseClassPlace place);

}
