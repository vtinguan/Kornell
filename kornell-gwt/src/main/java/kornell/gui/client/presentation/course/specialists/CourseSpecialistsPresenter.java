package kornell.gui.client.presentation.course.specialists;

import kornell.gui.client.content.SequencerFactory;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Widget;

public class CourseSpecialistsPresenter implements CourseSpecialistsView.Presenter{
	CourseSpecialistsView view;
	private CourseSpecialistsPlace place;
	
	public CourseSpecialistsPresenter(CourseSpecialistsView view,
							 PlaceController placeCtrl) {
		this.view = view;
		view.setPresenter(this);
	}

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	public void setPlace(CourseSpecialistsPlace place) {
		this.place = place;
	}
}
