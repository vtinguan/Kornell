package kornell.gui.client.presentation.welcome.summary;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.Enrollment;
import kornell.core.entity.EnrollmentState;
import kornell.core.entity.EntityFactory;
import kornell.core.to.CourseClassTO;
import kornell.core.to.CourseClassesTO;
import kornell.core.to.EnrollmentTO;
import kornell.core.to.TOFactory;
import kornell.gui.client.ClientFactory;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.event.CourseClassesFetchedEvent;
import kornell.gui.client.personnel.Student;
import kornell.gui.client.presentation.bar.MenuBarView;
import kornell.gui.client.presentation.classroom.ClassroomPlace;
import kornell.gui.client.presentation.welcome.summary.generic.GenericCourseSummaryView;

import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class CourseSummaryPresenter implements CourseSummaryView.Presenter{
	private CourseSummaryView view;
	private KornellSession session;
	private PlaceController placeCtrl;
	private static KornellConstants constants = GWT.create(KornellConstants.class);

	public CourseSummaryPresenter(KornellSession session, PlaceController placeCtrl, CourseClassTO courseClassTO, Student student) {
		this.session = session;
		this.placeCtrl = placeCtrl;
		view = new GenericCourseSummaryView(courseClassTO, student);
		view.setPresenter(this);
		initData();
	}
	
	@Override
	public void initData() {
	}

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	@Override
	public void courseSummaryClicked(CourseClassTO courseClassTO) {
		if(courseClassTO.getEnrollment() == null && courseClassTO.getCourseClass().isApproveEnrollmentsAutomatically()){
			requestEnrollment(courseClassTO);
			return;
		} else if (courseClassTO.getEnrollment() != null){
			session.setCurrentCourseClass(courseClassTO);
			placeCtrl.goTo(new ClassroomPlace(courseClassTO
					.getEnrollment().getUUID()));
		}
	}

	private void requestEnrollment(final CourseClassTO courseClassTO) {
		EntityFactory entityFactory = GWT.create(EntityFactory.class);
		Enrollment enrollment = entityFactory.enrollment().as();
		enrollment.setCourseClassUUID(courseClassTO.getCourseClass()
				.getUUID());
		enrollment.setPersonUUID(session.getCurrentUser().getPerson().getUUID());
		enrollment.setState(courseClassTO.getCourseClass().isApproveEnrollmentsAutomatically() ? EnrollmentState.enrolled : EnrollmentState.requested);
		session.enrollments().createEnrollment(enrollment,
				new Callback<Enrollment>() {
					@Override
					public void ok(Enrollment enrollment) {
						updateEnrollmentOnCourseClassTO(enrollment);
						placeCtrl.goTo(new ClassroomPlace(enrollment.getUUID()));
					}

					private void updateEnrollmentOnCourseClassTO(Enrollment enrollment) {
						TOFactory toFactory = GWT.create(TOFactory.class);
						EnrollmentTO enrollmentTO = toFactory.newEnrollmentTO().as();
						enrollmentTO.setEnrollment(enrollment);
						enrollmentTO.setPersonUUID(session.getCurrentUser().getPerson().getUUID());
						enrollmentTO.setFullName(session.getCurrentUser().getPerson().getFullName());
						enrollmentTO.setUsername(session.getCurrentUser().getUsername());
						courseClassTO.setEnrollment(enrollment);
						session.setCurrentCourseClass(courseClassTO);
					}
				});
	}

	@Override
	public void requestEnrollmentButtonClicked(CourseClassTO courseClassTO) {
		requestEnrollment(courseClassTO);
	}

}
