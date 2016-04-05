package kornell.gui.client.presentation.welcome.summary.generic;

import static kornell.core.util.StringUtils.mkurl;

import java.util.Date;

import kornell.core.entity.Course;
import kornell.core.entity.CourseClassState;
import kornell.core.entity.EnrollmentProgress;
import kornell.core.entity.EnrollmentProgressDescription;
import kornell.core.entity.EnrollmentState;
import kornell.core.to.CourseClassTO;
import kornell.core.util.StringUtils;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.personnel.Student;
import kornell.gui.client.presentation.welcome.summary.CourseSummaryView;
import kornell.gui.client.util.ClientConstants;
import kornell.gui.client.util.EnumTranslator;

import com.github.gwtbootstrap.client.ui.Heading;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.ProgressBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class GenericCourseSummaryView extends Composite implements CourseSummaryView{
	interface MyUiBinder extends UiBinder<Widget, GenericCourseSummaryView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private KornellConstants constants = GWT.create(KornellConstants.class);
	
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
	Paragraph pStatusErr;

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

	String ICON_COURSE_URL = mkurl(ClientConstants.IMAGES_PATH, "welcomeCourses");
	String iconCourseURL;

	private CourseClassTO courseClassTO;

	private Presenter presenter;

	private Student student;

	public GenericCourseSummaryView(final CourseClassTO courseClassTO, Student student){
		initWidget(uiBinder.createAndBindUi(this));

		this.courseClassTO = courseClassTO;
		this.student = student;
		init();
	}

	private void init() {
		Course course = courseClassTO.getCourseVersionTO().getCourse();
		hTitle.setText(course.getTitle());
		lblSubTitle.setText(constants.courseClass() + ": " + courseClassTO.getCourseClass().getName());
		pDescription.setText(course.getDescription());
		
		if(courseClassTO.getEnrollment() != null && EnrollmentState.cancelled.equals(courseClassTO.getEnrollment().getState())){
			pStatusErr.setText(constants.cancelledClassLabel());
			pStatusErr.removeStyleName("shy");
		}
		if(!CourseClassState.active.equals(courseClassTO.getCourseClass().getState())){
			pStatus.setText(constants.inactiveClassLabel());
			iconCourseURL = mkurl(ICON_COURSE_URL, "iconNotStarted.png");
		} else if (student.isEnrolled()) {
			onEnrolled(student);
		} else {
			onNotEnrolled();
		}
		onEnrolledOrNot();
	}

	private void onEnrolledOrNot() {
		imgThumb.setUrl(StringUtils.mkurl("/",courseClassTO.getCourseVersionTO().getDistributionURL(),courseClassTO.getCourseVersionTO().getCourseVersion().getDistributionPrefix(),"/images/thumb.jpg"));
		imgIconCourse.setUrl(iconCourseURL);

		sinkEvents(Event.ONCLICK);
		addHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.courseSummaryClicked(courseClassTO);
			}

		}, ClickEvent.getType());

	}

	private void onNotEnrolled() {
		Button requestEnrollmentBtn = getRequestEnrollmentButton();
		pnlCourseSummaryBar.add(requestEnrollmentBtn);

		pStatus.setText(constants.availableClassLabel());
		iconCourseURL = mkurl(ICON_COURSE_URL, "iconAcquire.png");
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

	private void onCourseInProgress(Integer progress) {
		if(progress >= 100){
			pStatus.setText(constants.pendingGradeLabel());
		} else {
			pStatus.setText(EnumTranslator.translateEnum(EnrollmentProgressDescription.inProgress)+": ");
			progressBar.removeStyleName("shy");
			progressBar.setPercent(progress);
			pStatusInfo.setText(progress + "% ");
		}
		iconCourseURL = mkurl(ICON_COURSE_URL, "iconInProgress.png");
		
	}

	private void onCourseNotStarted() {
		if(courseClassTO.getEnrollment() != null &&
				EnrollmentState.requested.equals(courseClassTO.getEnrollment().getState())){
			pStatus.setText(constants.pendingEnrollmentApproval());
			iconCourseURL = mkurl(ICON_COURSE_URL, "iconWaiting.png");
		} else {
			pStatus.setText(constants.toStart());
			iconCourseURL = mkurl(ICON_COURSE_URL, "iconNotStarted.png");
		}
	}

	private void onCourseCompleted(Date certifiedAt) {
		String statusText = EnumTranslator.translateEnum(EnrollmentProgressDescription.completed);
		if(certifiedAt != null) {
			statusText += " " + constants.completedOnToken() + ": ";
			pStatusInfo.setText(DateTimeFormat.getFormat("yyyy-MM-dd").format(certifiedAt));
		}
		if(courseClassTO.getCourseClass().getRequiredScore() != null && 
				courseClassTO.getCourseClass().getRequiredScore().intValue() != 0 &&
				courseClassTO.getEnrollment().getAssessmentScore() != null){
			pStatus2.setText(" - " + constants.completedCourseGradeLabel() + ":");
			pStatusInfo2.setText(""+courseClassTO.getEnrollment().getAssessmentScore().intValue());
		}
		pStatus.setText(statusText);
		iconCourseURL = mkurl(ICON_COURSE_URL, "iconFinished.png");
	}

	private Button getRequestEnrollmentButton() {
		Button requestEnrollmentBtn = new Button(courseClassTO.getCourseClass().isApproveEnrollmentsAutomatically() ? constants.startCourseLabel() : constants.requestEnrollmentLabel());
		requestEnrollmentBtn.addStyleName("right btnAction");

		requestEnrollmentBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.requestEnrollmentButtonClicked(courseClassTO);
			}
		});
		return requestEnrollmentBtn;
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
}
