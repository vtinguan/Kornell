package kornell.gui.client.presentation.course;

import java.util.logging.Logger;

import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.api.client.KornellSession;
import kornell.core.entity.Enrollment;
import kornell.core.entity.EnrollmentState;
import kornell.core.lom.Contents;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.Kornell;
import kornell.gui.client.presentation.util.LoadingPopup;
import kornell.gui.client.presentation.vitrine.VitrinePlace;
import kornell.gui.client.sequence.SequencerFactory;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class ClassroomPresenter implements ClassroomView.Presenter {
	Logger logger = Logger.getLogger(Kornell.class.getName());
	private ClassroomView view;
	private ClassroomPlace place;
	private PlaceController placeCtrl;
	private SequencerFactory sequencer;
	private KornellSession session;
	private EventBus bus;
	private Contents contents;

	public ClassroomPresenter(ClassroomView view,
			PlaceController placeCtrl,
			SequencerFactory seqFactory, KornellSession session, EventBus bus) {
		this.view = view;
		view.setPresenter(this);
		this.placeCtrl = placeCtrl;
		this.sequencer = seqFactory;
		this.bus = bus;
		this.session = session;		
	}

	private void displayPlace() {
		final String enrollmentUUID = place.getEnrollmentUUID();

		if(session.getCurrentUser() == null){
			placeCtrl.goTo(new VitrinePlace());
			return;
		}
		LoadingPopup.show();				
		session.enrollment(enrollmentUUID).contents(new Callback<Contents>() {
			@Override
			public void ok(Contents contents) {
				// check if user has a valid enrollment to this course
				boolean isEnrolled = false;
				UserInfoTO user = session.getCurrentUser();
				for (Enrollment enrollment : user.getEnrollmentsTO().getEnrollments()) {
					if(enrollment.getUUID().equals(enrollmentUUID)
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
