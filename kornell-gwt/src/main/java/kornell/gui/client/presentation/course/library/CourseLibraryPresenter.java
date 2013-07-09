package kornell.gui.client.presentation.course.library;

import kornell.gui.client.content.SequencerFactory;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.NodeList;

public class CourseLibraryPresenter implements CourseLibraryView.Presenter{
	CourseLibraryView view;
	private CourseLibraryPlace place;
	
	public CourseLibraryPresenter(CourseLibraryView view, PlaceController placeCtrl) {
		this.view = view;
		view.setPresenter(this);
	}

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	public void setPlace(CourseLibraryPlace place) {
		this.place = place;
	}
}
