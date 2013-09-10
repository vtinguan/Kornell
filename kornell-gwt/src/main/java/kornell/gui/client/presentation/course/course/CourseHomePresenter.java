package kornell.gui.client.presentation.course.course;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Widget;

public class CourseHomePresenter implements CourseHomeView.Presenter{
	CourseHomeView view;
	private CourseHomePlace place;
	
	public CourseHomePresenter(CourseHomeView view,
							 PlaceController placeCtrl) {
		this.view = view;
		view.setPresenter(this);
	}

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	public void setPlace(CourseHomePlace place) {
		this.place = place;
	}
}
