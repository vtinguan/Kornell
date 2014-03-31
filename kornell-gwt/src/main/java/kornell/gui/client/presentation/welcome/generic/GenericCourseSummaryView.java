package kornell.gui.client.presentation.welcome.generic;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.Course;
import kornell.core.entity.Enrollment;
import kornell.core.entity.EnrollmentProgress;
import static kornell.core.entity.EnrollmentProgress.*;
import kornell.core.entity.EnrollmentState;
import kornell.core.entity.EntityFactory;
import kornell.core.to.CourseClassTO;
import kornell.core.to.UserInfoTO;
import kornell.core.util.StringUtils;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.personnel.Student;
import kornell.gui.client.personnel.Teacher;
import kornell.gui.client.personnel.Teachers;
import kornell.gui.client.presentation.course.ClassroomPlace;

import com.github.gwtbootstrap.client.ui.Heading;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class GenericCourseSummaryView extends Composite {
	interface MyUiBinder extends UiBinder<Widget, GenericCourseSummaryView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private KornellConstants constants = GWT.create(KornellConstants.class);

	@UiField
	Heading hTitle;

	@UiField
	Paragraph pDescription;

	@UiField
	Paragraph pProgress;

	@UiField
	Image imgThumb;

	@UiField
	Image imgIconCourse;

	@UiField
	FlowPanel pnlCourseSummaryBar;

	String iconCourseURL = "skins/first/icons/";

	private CourseClassTO courseClassTO;
	private PlaceController placeCtrl;
	private KornellSession session;

	public GenericCourseSummaryView(final PlaceController placeCtrl,
			final CourseClassTO courseClassTO, KornellSession session) {
		initWidget(uiBinder.createAndBindUi(this));

		this.courseClassTO = courseClassTO;
		this.placeCtrl = placeCtrl;
		this.session = session;
		Course course = courseClassTO.getCourseVersionTO().getCourse();
		hTitle.setText("Curso: " + course.getTitle() + " - Turma: "
				+ courseClassTO.getCourseClass().getName());
		pDescription.setText(course.getDescription());

		final Teacher teacher = Teachers.of(courseClassTO);

		session.getCurrentUser(new Callback<UserInfoTO>() {
			@Override
			public void ok(UserInfoTO userInfoTO) {
				Student student = teacher.student(userInfoTO);
				if (student.isEnrolled())
					onEnrolled(student);
				else
					onNotEnrolled();
				onEnrolledOrNot();
			}

			private void onEnrolledOrNot() {
				String assetsURL = courseClassTO.getCourseVersionTO()
						.getDistributionURL()
						+ "/"
						+ courseClassTO.getCourseVersionTO().getCourseVersion()
								.getDistributionPrefix();
				imgThumb.setUrl(StringUtils.composeURL(assetsURL,
						"/images/thumb.jpg"));
				imgIconCourse.setUrl(iconCourseURL);

				sinkEvents(Event.ONCLICK);
				addHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						if (courseClassTO.getEnrollment() == null)
							return;
						placeCtrl.goTo(new ClassroomPlace(courseClassTO
								.getEnrollment().getUUID()));
					}
				}, ClickEvent.getType());

			}

			private void onNotEnrolled() {
				Button requestEnrollmentBtn = getRequestEnrollmentButton();
				pnlCourseSummaryBar.add(requestEnrollmentBtn);

				pProgress.setText(constants.toAcquire());
				iconCourseURL += "iconToAcquire.png";
			}

			private void onEnrolled(Student student) {
				EnrollmentProgress progress = student.getEnrollmentProgress();
				switch (progress.getDescription()) {
				case notStarted:
					onCourseNotStarted();
					break;
				case completed:
					onCourseCompleted();
					break;
				case inProgress:
					onCourseInProgress(progress.getProgress());
					break;
				}
			}
		});

	}

	private void onCourseInProgress(Integer progress) {
		pProgress.setText(progress + "% " + constants.complete().toLowerCase());
		iconCourseURL += "iconCurrent.png";
	}

	private void onCourseNotStarted() {
		pProgress.setText(constants.toStart());
		iconCourseURL += "iconToStart.png";
	}

	private void onCourseCompleted() {
		/*
		 * Label certificate = new Label(constants.certificate());
		 * certificate.addStyleName("courseProgress");
		 * certificate.addStyleName("courseProgressCertificate");
		 * pnlCourseSummaryBar.add(certificate);
		 * 
		 * Image iconCertificate = new Image();
		 * iconCertificate.setUrl(iconCourseURL+"iconPDF.png");
		 * iconCertificate.addStyleName("iconCertificate");
		 * pnlCourseSummaryBar.add(iconCertificate);
		 */

		pProgress.setText(constants.courseFinished());
		iconCourseURL += "iconFinished.png";
	}

	private Button getRequestEnrollmentButton() {
		Button requestEnrollmentBtn = new Button("Solicitar Matrícula");
		requestEnrollmentBtn.addStyleName("right btnAction");

		requestEnrollmentBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				EntityFactory entityFactory = GWT.create(EntityFactory.class);
				Enrollment enrollment = entityFactory.newEnrollment().as();
				enrollment.setCourseClassUUID(courseClassTO.getCourseClass()
						.getUUID());
				enrollment.setPerson(session.getCurrentUser().getPerson());
				enrollment.setState(EnrollmentState.requested);
				session.createEnrollment(enrollment,
						new Callback<Enrollment>() {
							@Override
							public void ok(Enrollment to) {
								session.getCurrentUser().getEnrollmentsTO()
										.getEnrollments().add(to);
								for (CourseClassTO courseClassTO : Dean
										.getInstance().getCourseClassesTO()
										.getCourseClasses()) {
									if (courseClassTO.getCourseClass()
											.getUUID()
											.equals(to.getCourseClassUUID())) {
										Dean.getInstance().setCourseClassTO(
												courseClassTO);
										break;
									}
								}
								placeCtrl.goTo(new ClassroomPlace(to.getUUID()));
							}
						});
			}
		});
		return requestEnrollmentBtn;
	}
}
