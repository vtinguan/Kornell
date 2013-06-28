package kornell.gui.client.presentation.course;

import kornell.gui.client.content.SequencerFactory;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.NodeList;

public class CoursePresenter implements CourseView.Presenter{
	CourseView view;
	private CoursePlace place;
	private SequencerFactory sequencer;
	
	public CoursePresenter(CourseView view,
							 PlaceController placeCtrl) {
		this.view = view;
		view.setPresenter(this);
	}

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	public void setPlace(CoursePlace place) {
		this.place = place;
	}
}
