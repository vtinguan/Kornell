package kornell.gui.client.presentation.course;

import kornell.gui.client.sequence.SequencerFactory;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class CoursePresenter implements CourseView.Presenter{
	CourseView view;
	private CoursePlace place;
	private SequencerFactory sequencer;
	
	public CoursePresenter(CourseView view,
							 PlaceController placeCtrl,
							 SequencerFactory sequencer) {		
		this.view = view;
		view.setPresenter(this);
		this.sequencer = sequencer;		
	}
	

	private void displayPlace() {
		sequencer.at(place).displayOn(getPanel());
	}


	private FlowPanel getPanel() {
		return view.getContentPanel();
	}


	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	public void setPlace(CoursePlace place) {
		this.place = place;
		displayPlace();
	}
}
