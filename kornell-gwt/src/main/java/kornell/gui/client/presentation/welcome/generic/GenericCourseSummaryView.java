package kornell.gui.client.presentation.welcome.generic;

import java.util.Date;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.Course;
import kornell.core.entity.Enrollment;
import kornell.core.entity.EnrollmentProgress;
import kornell.core.entity.EnrollmentProgressDescription;
import kornell.core.entity.EnrollmentState;
import kornell.core.entity.EntityFactory;
import kornell.core.to.CourseClassTO;
import kornell.core.to.EnrollmentTO;
import kornell.core.to.TOFactory;
import kornell.core.to.UserInfoTO;
import kornell.core.util.StringUtils;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.personnel.Student;
import kornell.gui.client.personnel.Teacher;
import kornell.gui.client.personnel.Teachers;
import kornell.gui.client.presentation.course.ClassroomPlace;
import kornell.gui.client.presentation.util.FormHelper;

import com.github.gwtbootstrap.client.ui.Heading;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.ProgressBar;
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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class GenericCourseSummaryView extends Composite {
	interface MyUiBinder extends UiBinder<Widget, GenericCourseSummaryView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private KornellConstants constants = GWT.create(KornellConstants.class);
	
	private FormHelper formHelper = GWT.create(FormHelper.class);

	@UiField
	Heading hTitle;
	
	@UiField
	Label lblSubTitle;

	@UiField
	Paragraph pDescription;

	@UiField
	Paragraph pStatus;
	
	@UiField
	Paragraph pStatusInfo;

	@UiField
	Paragraph pStatus2;
	
	@UiField
	Paragraph pStatusInfo2;
	
	@UiField
	ProgressBar progressBar;

	@UiField
	Image imgThumb;

	@UiField
	Image imgIconCourse;

	@UiField
	FlowPanel pnlCourseSummaryBar;

	String iconCourseURL = "skins/first/icons/welcomeCourses/";

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
		hTitle.setText(course.getTitle());
		lblSubTitle.setText("Turma: " + courseClassTO.getCourseClass().getName());
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
				String assetsURL = StringUtils.composeURL(courseClassTO.getCourseVersionTO().getDistributionURL(),
						courseClassTO.getCourseVersionTO().getCourseVersion().getDistributionPrefix());
				imgThumb.setUrl(StringUtils.composeURL(assetsURL, "/images/thumb.jpg"));
				imgIconCourse.setUrl(iconCourseURL);

				sinkEvents(Event.ONCLICK);
				addHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						if (courseClassTO.getEnrollment() == null)
							return;
						Dean.getInstance().setCourseClassTO(courseClassTO);
						placeCtrl.goTo(new ClassroomPlace(courseClassTO
								.getEnrollment().getUUID()));
					}
				}, ClickEvent.getType());

			}

			private void onNotEnrolled() {
				Button requestEnrollmentBtn = getRequestEnrollmentButton();
				pnlCourseSummaryBar.add(requestEnrollmentBtn);

				pStatus.setText("Disponível");
				iconCourseURL += "iconAcquire.png";
			}

			private void onEnrolled(Student student) {
				EnrollmentProgress progress = student.getEnrollmentProgress();
				switch (progress.getDescription()) {
				case notStarted:
					onCourseNotStarted();
					break;
				case completed:
					onCourseCompleted(progress.getCertifiedAt());
					break;
				case inProgress:
					onCourseInProgress(progress.getProgress());
					break;
				}
			}
		});

	}

	private void onCourseInProgress(Integer progress) {
		if(progress >= 100){
			pStatus.setText("Aguardando Avaliação");
		} else {
			pStatus.setText(formHelper.getEnrollmentProgressAsText(EnrollmentProgressDescription.inProgress)+": ");
			progressBar.removeStyleName("shy");
			progressBar.setPercent(progress);
			pStatusInfo.setText(progress + "% ");
		}
		iconCourseURL += "iconInProgress.png";
		
	}

	private void onCourseNotStarted() {
		if(courseClassTO.getEnrollment() != null &&
				EnrollmentState.requested.equals(courseClassTO.getEnrollment().getState())){
			pStatus.setText("Aguardando aprovação da matrícula");
			iconCourseURL += "iconWaiting.png";
		} else {
			pStatus.setText(constants.toStart());
			iconCourseURL += "iconNotStarted.png";
		}
	}

	private void onCourseCompleted(String certifiedAt) {
		String statusText = formHelper.getEnrollmentProgressAsText(EnrollmentProgressDescription.completed);
		if(certifiedAt != null) {
			statusText += " em: ";
			pStatusInfo.setText(formHelper.getStringFromDate(certifiedAt));
		}
		if(courseClassTO.getCourseClass().getRequiredScore() != null && 
				courseClassTO.getCourseClass().getRequiredScore().intValue() != 0 &&
				courseClassTO.getEnrollment().getAssessmentScore() != null){
			pStatus2.setText(" - Nota: ");
			pStatusInfo2.setText(""+courseClassTO.getEnrollment().getAssessmentScore().intValue());
		}
		pStatus.setText(statusText);
		iconCourseURL += "iconFinished.png";
	}

	private Button getRequestEnrollmentButton() {
		Button requestEnrollmentBtn = new Button("Solicitar Matrícula");
		requestEnrollmentBtn.addStyleName("right btnAction");

		requestEnrollmentBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				EntityFactory entityFactory = GWT.create(EntityFactory.class);
				Enrollment enrollment = entityFactory.enrollment().as();
				enrollment.setCourseClassUUID(courseClassTO.getCourseClass()
						.getUUID());
				enrollment.setPersonUUID(session.getCurrentUser().getPerson().getUUID());
				enrollment.setState(EnrollmentState.requested);
				session.enrollments().createEnrollment(enrollment,
						new Callback<Enrollment>() {
							@Override
							public void ok(Enrollment enrollment) {
								TOFactory toFactory = GWT.create(TOFactory.class);
								EnrollmentTO enrollmentTO = toFactory.newEnrollmentTO().as();
								enrollmentTO.setEnrollment(enrollment);
								enrollmentTO.setPersonUUID(session.getCurrentUser().getPerson().getUUID());
								enrollmentTO.setFullName(session.getCurrentUser().getPerson().getFullName());
								enrollmentTO.setUsername(session.getCurrentUser().getUsername());
								session.getCurrentUser().getEnrollments().getEnrollments().add(enrollment);
								for (CourseClassTO courseClassTO : Dean.getInstance().getCourseClassesTO().getCourseClasses()) {
									if (courseClassTO.getCourseClass().getUUID().equals(enrollment.getCourseClassUUID())) {
										courseClassTO.setEnrollment(enrollment);
										Dean.getInstance().setCourseClassTO(courseClassTO);
										break;
									}
								}
								placeCtrl.goTo(new ClassroomPlace(enrollment.getUUID()));
							}
						});
			}
		});
		return requestEnrollmentBtn;
	}
}
