package kornell.gui.client.presentation.course;

import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.api.client.UserSession;
import kornell.core.entity.Enrollment;
import kornell.core.entity.EnrollmentState;
import kornell.core.lom.Contents;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.event.ShowDetailsEvent;
import kornell.gui.client.presentation.util.LoadingPopup;
import kornell.gui.client.sequence.SequencerFactory;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class ClassroomPresenter implements ClassroomView.Presenter {
	private ClassroomView view;
	private ClassroomPlace place;
	private SequencerFactory sequencer;
	private KornellClient client;
	private EventBus bus;
	private UserSession session;
	private Contents contents;

	public ClassroomPresenter(ClassroomView view,
			PlaceController placeCtrl,
			SequencerFactory seqFactory, KornellClient client, EventBus bus, UserSession session) {
		this.view = view;
		view.setPresenter(this);
		this.sequencer = seqFactory;
		this.client = client;
		this.bus = bus;
		this.session = session;
	}

	private void displayPlace() {
		LoadingPopup.show();
		client.enrollment(place.getEnrollmentUUID()).contents(new Callback<Contents>() {
			@Override
			public void ok(Contents contents) {
				// check if user has a valid enrollment to this course
				boolean isEnrolled = false;
				UserInfoTO user = session.getUserInfo();
				for (Enrollment enrollment : user.getEnrollmentsTO().getEnrollments()) {
					if(enrollment.getUUID().equals(place.getEnrollmentUUID())
							&& (EnrollmentState.enrolled.equals(enrollment.getState()) ||
									(EnrollmentState.enrolled.equals(enrollment.getState())))){
						isEnrolled = true;
						break;
					}
				}

				LoadingPopup.hide();
				setContents(contents);
				view.display(isEnrolled);
				sequencer.withPlace(place)
						.withPanel(getPanel())
						.go(contents);
				
			}

		});
	}

	private FlowPanel getPanel() {
		return view.getContentPanel();
	}
	
	@Override
	public Contents getContents(){
		return contents;
	}
	
	private void setContents(Contents contents){
		this.contents = contents;
	}

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	public void setPlace(ClassroomPlace place) {
		this.place = place;
		displayPlace();
	}
}
