package kornell.gui.client.presentation.course;

import java.util.List;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.CourseClass;
import kornell.core.entity.CourseClassState;
import kornell.core.entity.Enrollment;
import kornell.core.entity.EnrollmentState;
import kornell.core.entity.EnrollmentsEntries;
import kornell.core.lom.Contents;
import kornell.core.to.CourseClassTO;
import kornell.core.to.EnrollmentLaunchTO;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.GenericClientFactoryImpl;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.event.HideSouthBarEvent;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.presentation.profile.ProfilePlace;
import kornell.gui.client.presentation.util.KornellNotification;
import kornell.gui.client.presentation.util.LoadingPopup;
import kornell.gui.client.presentation.vitrine.VitrinePlace;
import kornell.gui.client.sequence.Sequencer;
import kornell.gui.client.sequence.SequencerFactory;
import kornell.scorm.client.scorm12.SCORM12Runtime;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class ClassroomPresenter implements ClassroomView.Presenter {
	
	private static KornellConstants constants = GWT.create(KornellConstants.class);

	private ClassroomView view;
	private ClassroomPlace place;
	private PlaceController placeCtrl;
	private SequencerFactory sequencerFactory;
	private KornellSession session;
	private Contents contents;
	private Sequencer sequencer;
	private EventBus bus;

	public ClassroomPresenter(EventBus bus, ClassroomView view, PlaceController placeCtrl,
			SequencerFactory seqFactory, KornellSession session) {
		this.bus = bus;
		this.view = view;
		view.setPresenter(this);
		this.placeCtrl = placeCtrl;
		this.sequencerFactory = seqFactory;
		this.session = session;
	}

	private void displayPlace() {
		final String enrollmentUUID = place.getEnrollmentUUID();

		if(session.isAnonymous()){
			placeCtrl.goTo(new VitrinePlace());
			return;
		}

		boolean isEnrolled = false;
		String enrollmentClassUUID = null;
		UserInfoTO user = session.getCurrentUser();
		//TODO: Consider moving this to the server
		final Dean dean = Dean.getInstance();
		List<Enrollment> enrollments = user.getEnrollments().getEnrollments();
		for (Enrollment enrollment : enrollments) {
			String eUUID = enrollment.getUUID();					
			if(eUUID.equals(enrollmentUUID)){
				enrollmentClassUUID = enrollment.getCourseClassUUID();
				dean.setCourseClassTO(enrollmentClassUUID);
				if(dean.getCourseClassTO() != null && dean.getCourseClassTO().getCourseClass().isInvisible()){
					dean.setCourseClassTO((CourseClassTO)null);
					KornellNotification.show(constants.classSetAsInvisible(), AlertType.WARNING, 5000);
					placeCtrl.goTo(new ProfilePlace(session.getCurrentUser().getPerson().getUUID(), false));
					return;
				}
				isEnrolled = EnrollmentState.enrolled.equals(enrollment.getState());
				break;
			}
		}
		if(enrollmentClassUUID == null){
			dean.setCourseClassTO((CourseClassTO)null);
		}
		view.asWidget().setVisible(false);
		
		CourseClass courseClass = Dean.getInstance().getCourseClassTO() != null ? Dean.getInstance().getCourseClassTO().getCourseClass() : null;
		CourseClassState courseClassState = courseClass != null ? courseClass.getState() : null;
		
		//TODO: Consider if the null state is inactive				
        final boolean showCourseClassContent = enrollmentClassUUID == null || (isEnrolled && (courseClassState != null && !CourseClassState.inactive.equals(courseClassState)));

		
		LoadingPopup.show();		
		final PopupPanel popup = KornellNotification.show(constants.loadingTheCourse(), AlertType.WARNING, -1);
		session.enrollment(enrollmentUUID).launch(new Callback<EnrollmentLaunchTO>() {
			
			public void ok(EnrollmentLaunchTO to) {
				popup.hide();
				loadRuntime(to.getEnrollmentEntries());
				loadContents(enrollmentUUID,to.getContents());
			};
			
			private void loadRuntime(EnrollmentsEntries enrollmentEntries) {
				SCORM12Runtime.launch(bus, session,placeCtrl, enrollmentEntries);
			}

			private void loadContents(final String enrollmentUUID, final Contents contents) {
				// check if user has a valid enrollment to this course
				GenericClientFactoryImpl.EVENT_BUS.fireEvent(new HideSouthBarEvent());
				setContents(contents);
				view.display(showCourseClassContent);	
				view.asWidget().setVisible(true);
				LoadingPopup.hide();
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
		if (sequencer != null)
			sequencer.stop();
	}

	@Override
	public Contents getContents() {
		return contents;
	}

	private void setContents(Contents contents) {
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
		if (sequencer != null)
			sequencer.fireProgressEvent();
	}
}
