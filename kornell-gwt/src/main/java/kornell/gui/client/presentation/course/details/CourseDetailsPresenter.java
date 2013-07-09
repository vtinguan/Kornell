package kornell.gui.client.presentation.course.details;

import kornell.gui.client.content.SequencerFactory;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.NodeList;

public class CourseDetailsPresenter implements CourseDetailsView.Presenter{
	CourseDetailsView view;
	private CourseDetailsPlace place;
	private SequencerFactory sequencer;
	
	public CourseDetailsPresenter(CourseDetailsView view, PlaceController placeCtrl) {
		this.view = view;
		view.setPresenter(this);
	}

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	public void setPlace(CourseDetailsPlace place) {
		this.place = place;
	}
}
