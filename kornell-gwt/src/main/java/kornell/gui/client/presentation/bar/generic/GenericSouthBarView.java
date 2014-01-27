package kornell.gui.client.presentation.bar.generic;

import kornell.gui.client.ClientFactory;
import kornell.gui.client.presentation.admin.AdminPlace;
import kornell.gui.client.presentation.bar.ActivityBarView;
import kornell.gui.client.presentation.bar.AdminBarView;
import kornell.gui.client.presentation.bar.CourseBarView;
import kornell.gui.client.presentation.bar.SouthBarView;
import kornell.gui.client.presentation.course.ClassroomPlace;
import kornell.gui.client.presentation.course.chat.CourseChatPlace;
import kornell.gui.client.presentation.course.course.CourseHomePlace;
import kornell.gui.client.presentation.course.details.CourseDetailsPlace;
import kornell.gui.client.presentation.course.forum.CourseForumPlace;
import kornell.gui.client.presentation.course.library.CourseLibraryPlace;
import kornell.gui.client.presentation.course.specialists.CourseSpecialistsPlace;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class GenericSouthBarView extends Composite implements SouthBarView {

	interface MyUiBinder extends UiBinder<Widget, GenericSouthBarView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private ActivityBarView activityBarView;

	private CourseBarView courseBarView;
	
	private AdminBarView adminBarView;

	private boolean visible = false;

	private PlaceController placeCtrl;

	@UiField
	FlowPanel southBar;

	private ClientFactory clientFactory;

	public GenericSouthBarView(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		initWidget(uiBinder.createAndBindUi(this));

		clientFactory.getEventBus().addHandler(PlaceChangeEvent.TYPE, new PlaceChangeEvent.Handler() {
			@Override
			public void onPlaceChange(PlaceChangeEvent event) {
				Place newPlace = event.getNewPlace();
				if(newPlace instanceof AdminPlace){
					southBar.clear();
					//southBar.add(getAdminBarView(newPlace));
					visible = false;
				} else if (newPlace instanceof CourseDetailsPlace
						|| newPlace instanceof ClassroomPlace) {
					southBar.clear();
					southBar.add(getActivityBarView());
					visible = true;
				} else if (newPlace instanceof CourseHomePlace
						|| newPlace instanceof CourseLibraryPlace
						|| newPlace instanceof CourseForumPlace
						|| newPlace instanceof CourseChatPlace
						|| newPlace instanceof CourseSpecialistsPlace) {
					southBar.clear();
					southBar.add(getCourseBarView(newPlace));
					visible = true;
				} else {
					visible = false;
				}

			}
		});
	}

	private ActivityBarView getActivityBarView() {
		if (activityBarView == null)
			activityBarView = new GenericActivityBarView(clientFactory);
		return activityBarView;
	}

	private CourseBarView getCourseBarView(Place newPlace) {
		if (courseBarView == null)
			courseBarView = new GenericCourseBarView(clientFactory.getEventBus(), clientFactory.getPlaceController());
		courseBarView.updateSelection(newPlace);
		return courseBarView;
	}

	private AdminBarView getAdminBarView(Place newPlace) {
		if (adminBarView == null)
			adminBarView = new GenericAdminBarView(clientFactory);
		return adminBarView;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public void setPresenter(Presenter presenter) {
	}

}
