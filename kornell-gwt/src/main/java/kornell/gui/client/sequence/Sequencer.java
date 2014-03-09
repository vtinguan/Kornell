package kornell.gui.client.sequence;

import kornell.core.lom.Contents;
import kornell.gui.client.presentation.course.ClassroomPlace;

import com.google.gwt.user.client.ui.FlowPanel;

public interface Sequencer 
	extends NavigationRequest.Handler  {
	Sequencer withPanel(FlowPanel contentPanel);
	Sequencer withPlace(ClassroomPlace place);
	void go(Contents contents);
	void stop();
	void fireProgressEvent();
}
