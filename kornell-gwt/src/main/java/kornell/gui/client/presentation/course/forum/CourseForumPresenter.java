package kornell.gui.client.presentation.course.forum;

import kornell.gui.client.sequence.SequencerFactory;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Widget;

public class CourseForumPresenter implements CourseForumView.Presenter{
	CourseForumView view;
	private CourseForumPlace place;
	private SequencerFactory sequencer;
	
	public CourseForumPresenter(CourseForumView view,
							 PlaceController placeCtrl) {
		this.view = view;
		view.setPresenter(this);
	}

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	public void setPlace(CourseForumPlace place) {
		this.place = place;
	}
}
