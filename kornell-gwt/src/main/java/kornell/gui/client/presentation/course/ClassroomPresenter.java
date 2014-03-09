package kornell.gui.client.presentation.course;

import java.util.logging.Logger;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.Enrollment;
import kornell.core.entity.EnrollmentState;
import kornell.core.lom.Contents;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.Kornell;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.presentation.util.LoadingPopup;
import kornell.gui.client.presentation.vitrine.VitrinePlace;
import kornell.gui.client.sequence.Sequencer;
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
	private SequencerFactory sequencerFactory;
	private KornellSession session;
	private EventBus bus;
	private Contents contents;
	private boolean sequencerInitialized = false;
	private Sequencer sequencer;

	public ClassroomPresenter(ClassroomView view,
			PlaceController placeCtrl,
			SequencerFactory seqFactory, KornellSession session, EventBus bus) {
		this.view = view;
		view.setPresenter(this);
		this.placeCtrl = placeCtrl;
		this.sequencerFactory = seqFactory;
		this.bus = bus;
		this.session = session;		
	}

	private void displayPlace() {
		final String enrollmentUUID = place.getEnrollmentUUID();

		if(session.isAnonymous()){
			placeCtrl.goTo(new VitrinePlace());
			return;
		}
		view.asWidget().setVisible(false);
		LoadingPopup.show();				
		session.enrollment(enrollmentUUID).contents(new Callback<Contents>() {
			@Override
			public void ok(Contents contents) {
				// check if user has a valid enrollment to this course
				boolean isEnrolled = false;
				UserInfoTO user = session.getCurrentUser();
				for (Enrollment enrollment : user.getEnrollmentsTO().getEnrollments()) {
					if(enrollment.getUUID().equals(enrollmentUUID)){
						Dean.getInstance().setCourseClassTO(enrollment.getCourseClassUUID());
						if(EnrollmentState.enrolled.equals(enrollment.getState()) ||
									(EnrollmentState.preEnrolled.equals(enrollment.getState()))){
							isEnrolled = true;
						}
						break;
					}
				}
				LoadingPopup.hide();
				setContents(contents);
				view.display(isEnrolled);		
				view.asWidget().setVisible(true);
			}

		});
	}

	private FlowPanel getPanel() {
		return view.getContentPanel();
	}

	@Override
	public void startSequencer() {
		sequencer = sequencerFactory.withPlace(place).withPanel(getPanel());
		sequencer.go(contents);
	}

	@Override
	public void stopSequencer() {
		if(sequencer != null)
			sequencer.stop();
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

	@Override
	public void fireProgressEvent() {
		if(sequencer != null)
			sequencer.fireProgressEvent();
	}
}
