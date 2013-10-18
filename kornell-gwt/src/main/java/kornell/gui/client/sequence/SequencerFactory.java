package kornell.gui.client.sequence;

import kornell.gui.client.presentation.course.CoursePlace;


public interface SequencerFactory {

	Sequencer withPlace(CoursePlace place);

}
