package kornell.gui.client.presentation.course;

import java.util.List;
import java.util.logging.Logger;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.CourseClass;
import kornell.core.entity.CourseClassState;
import kornell.core.entity.Enrollment;
import kornell.core.entity.EnrollmentEntries;
import kornell.core.entity.EnrollmentState;
import kornell.core.lom.Contents;
import kornell.core.to.CourseClassTO;
import kornell.core.to.EnrollmentLaunchTO;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.GenericClientFactoryImpl;
import kornell.gui.client.event.HideSouthBarEvent;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.presentation.util.KornellNotification;
import kornell.gui.client.presentation.util.LoadingPopup;
import kornell.gui.client.presentation.vitrine.VitrinePlace;
import kornell.gui.client.sequence.Sequencer;
import kornell.gui.client.sequence.SequencerFactory;
import kornell.scorm.client.scorm12.SCORM12Runtime;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class ClassroomPresenter implements ClassroomView.Presenter {
	
	Logger logger = Logger.getLogger(ClassroomPresenter.class.getName());
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
		
		view.asWidget().setVisible(false);
		LoadingPopup.show();				
		session.enrollment(enrollmentUUID).launch(new Callback<EnrollmentLaunchTO>() {
			
			public void ok(EnrollmentLaunchTO to) {
				loadRuntime(to.getEnrollmentEntries());
				loadContents(enrollmentUUID,to.getContents());
			};
			
						
			
			
			private void loadRuntime(EnrollmentEntries enrollmentEntries) {
				SCORM12Runtime.launch(bus, enrollmentEntries);
			}

			private void loadContents(final String enrollmentUUID,
					final Contents contents) {
				//TODO: UGLY DESPERATE HACK due to the courseClasses not yet set on dean
				Timer timer = new Timer() { 
					public void run(){				
						// check if user has a valid enrollment to this course
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
								isEnrolled = EnrollmentState.enrolled.equals(enrollment.getState());
								break;
							}
						}
						if(enrollmentClassUUID == null){
							dean.setCourseClassTO((CourseClassTO)null);
						}
						GenericClientFactoryImpl.EVENT_BUS.fireEvent(new HideSouthBarEvent());
						final boolean hasEnrolled = isEnrolled;
						
						CourseClassTO courseClassTO = dean != null ? dean.getCourseClassTO() : null;
						CourseClass courseClass = courseClassTO != null ? courseClassTO.getCourseClass() : null;
						CourseClassState courseClassState = courseClass != null ? courseClass.getState() : null;
						
						//TODO: Consider if the null state is inactive				
						boolean isInactiveCourseClass = courseClassState != null && CourseClassState.inactive.equals(courseClassState);
						LoadingPopup.hide();
						setContents(contents);
						view.display(hasEnrolled && !isInactiveCourseClass);		
						view.asWidget().setVisible(true);
					}
				};
				timer.schedule(1000);
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
