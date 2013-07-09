package kornell.gui.client.presentation.course.notes;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Widget;

public class CourseNotesPresenter implements CourseNotesView.Presenter{
	CourseNotesView view;
	private CourseNotesPlace place;
	
	public CourseNotesPresenter(CourseNotesView view,
							 PlaceController placeCtrl) {
		this.view = view;
		view.setPresenter(this);
	}

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	public void setPlace(CourseNotesPlace place) {
		this.place = place;
	}
}
