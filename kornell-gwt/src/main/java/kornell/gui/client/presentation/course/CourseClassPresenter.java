package kornell.gui.client.presentation.course;

import kornell.gui.client.sequence.SequencerFactory;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class CourseClassPresenter implements CourseClassView.Presenter{
	CourseClassView view;
	private CourseClassPlace place;
	private SequencerFactory sequencer;
	
	public CourseClassPresenter(CourseClassView view,
							 PlaceController placeCtrl,
							 SequencerFactory sequencer) {		
		this.view = view;
		view.setPresenter(this);
		this.sequencer = sequencer;		
	}
	

	private void displayPlace() {	
		sequencer.withPlace(place).withPanel(getPanel()).go();
	}


	private FlowPanel getPanel() {
		return view.getContentPanel();
	}


	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	public void setPlace(CourseClassPlace place) {
		this.place = place;
		displayPlace();
	}
}
