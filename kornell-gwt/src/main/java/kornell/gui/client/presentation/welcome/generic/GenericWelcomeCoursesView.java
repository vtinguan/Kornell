package kornell.gui.client.presentation.welcome.generic;

import java.util.ArrayList;
import java.util.List;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.Enrollment;
import kornell.core.entity.EnrollmentProgressDescription;
import static kornell.core.entity.EnrollmentProgressDescription.*;
import kornell.core.to.CourseClassTO;
import kornell.core.to.CourseClassesTO;
import kornell.core.to.EnrollmentsTO;
import kornell.core.to.TOFactory;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.personnel.Student;
import kornell.gui.client.personnel.Teacher;
import kornell.gui.client.personnel.Teachers;
import kornell.gui.client.presentation.util.KornellNotification;
import kornell.gui.client.presentation.welcome.WelcomeView;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

//TODO - Courses will overflow the screen

public class GenericWelcomeCoursesView extends Composite implements WelcomeView {
	interface MyUiBinder extends UiBinder<Widget, GenericWelcomeCoursesView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	FlowPanel coursesPanel;
	@UiField
	FlowPanel pnlCourses;
	@UiField
	Button btnCoursesAll;
	@UiField
	Button btnCoursesInProgress;
	@UiField
	Button btnCoursesToStart;
	@UiField
	Button btnCoursesToAcquire;
	@UiField
	Button btnCoursesFinished;

	private static String COURSES_ALL = "all";
	private static String COURSES_IN_PROGRESS = "inProgress";
	private static String COURSES_TO_START = "toStart";
	private static String COURSES_TO_ACQUIRE = "toAcquire";
	private static String COURSES_FINISHED = "finished";

	private KornellSession session;

	private PlaceController placeCtrl;

	private String displayCourses;

	private KornellConstants constants = GWT.create(KornellConstants.class);

	public TOFactory toFactory;
	private WelcomeView.Presenter presenter;

	public GenericWelcomeCoursesView(EventBus bus, KornellSession session,
			PlaceController placeCtrl, TOFactory toFactory) {
		this.session = session;
		this.placeCtrl = placeCtrl;
		this.toFactory = toFactory;
		initWidget(uiBinder.createAndBindUi(this));
		coursesPanel.setVisible(false);
		btnCoursesAll.setText(constants.allCourses());
		btnCoursesInProgress.setText(constants.inProgress());
		btnCoursesToStart.setText(constants.toStart());
		btnCoursesToAcquire.setText("Disponíveis");
		btnCoursesFinished.setText(constants.finished());
		initData();
	}

	private void initData() {
		refreshButtonsSelection();
		session.getCourseClassesTOByInstitution(Dean.getInstance()
				.getInstitution().getUUID(), new Callback<CourseClassesTO>() {
			@Override
			public void ok(CourseClassesTO tos) {
				Dean.getInstance().setCourseClassesTO(tos);
				if (displayCourses == null)
					displayCourses = COURSES_ALL;
				updateUserEnrollments(tos);
				display(tos);
			}
		});
	}

	private void updateUserEnrollments(CourseClassesTO tos) {
		EnrollmentsTO enrollmentsTO = toFactory.newEnrollmentsTO().as();
		List<Enrollment> enrollments = new ArrayList<Enrollment>();
		for (CourseClassTO courseClassTO : tos.getCourseClasses()) {
			if (courseClassTO.getEnrollment() != null) {
				enrollments.add(courseClassTO.getEnrollment());
			}
		}
		enrollmentsTO.setEnrollments(enrollments);
		session.getCurrentUser().setEnrollmentsTO(enrollmentsTO);
	}

	private void display(final CourseClassesTO tos) {
		pnlCourses.clear();
		if(tos.getCourseClasses().size() == 0){
			coursesPanel.setVisible(false);
			KornellNotification.show("Você não está matriculado em alguma turma e não há turmas disponíveis para solicitar uma nova matrícula.", AlertType.INFO, 8000);
		} else {
			coursesPanel.setVisible(true);
		}
		btnCoursesAll.setVisible(true);
		btnCoursesFinished.setVisible(false);
		btnCoursesInProgress.setVisible(false);
		btnCoursesToStart.setVisible(false);
		btnCoursesToAcquire.setVisible(false);
		refreshButtonsSelection();
		
		for (final CourseClassTO courseClassTO : tos.getCourseClasses()) {
			final Teacher teacher = Teachers.of(courseClassTO);

			session.getCurrentUser(new Callback<UserInfoTO>() {
				@Override
				public void ok(UserInfoTO userInfoTO) {
					Student student = teacher.student(userInfoTO);
					if (student.isEnrolled()) {
						EnrollmentProgressDescription description = student.getEnrollmentProgress().getDescription();
						switch (description) {
						case completed:
							addPanelIfFiltered(COURSES_FINISHED, courseClassTO);
							btnCoursesFinished.setVisible(true);
							break;
						case inProgress:
							addPanelIfFiltered(COURSES_IN_PROGRESS, courseClassTO);
							btnCoursesInProgress.setVisible(true);
							break;
						case notStarted:
							addPanelIfFiltered(COURSES_TO_START, courseClassTO);
							btnCoursesToStart.setVisible(true);
							break;
						}
					} else {
						addPanelIfFiltered(COURSES_TO_ACQUIRE, courseClassTO);
						btnCoursesToAcquire.setVisible(true);
					}
					display(tos);
				}
				
				private void addPanelIfFiltered(String filter, CourseClassTO courseClassTO){
					if(filter != null && (COURSES_ALL.equals(displayCourses) || filter.equals(displayCourses))){
						pnlCourses.add(new GenericCourseSummaryView(placeCtrl, courseClassTO, session));
						GWT.log("Added: " + courseClassTO.getCourseClass().getName());
					}
				}

				private void display(CourseClassesTO tos) {
					if(tos.getCourseClasses().size() <= 1){
						btnCoursesAll.setVisible(false);
						btnCoursesInProgress.setVisible(false);
						btnCoursesToStart.setVisible(false);
						btnCoursesToAcquire.setVisible(false);
						btnCoursesFinished.setVisible(false);
					}					
				}
			});
		}

	}


	@UiHandler("btnCoursesAll")
	void handleClickAll(ClickEvent e) {
		this.displayCourses = COURSES_ALL;
		initData();
	}

	@UiHandler("btnCoursesInProgress")
	void handleClickInProgress(ClickEvent e) {
		this.displayCourses = COURSES_IN_PROGRESS;
		initData();
	}

	@UiHandler("btnCoursesToAcquire")
	void handleClickToAcquire(ClickEvent e) {
		this.displayCourses = COURSES_TO_ACQUIRE;
		initData();
	}

	@UiHandler("btnCoursesToStart")
	void handleClickToStart(ClickEvent e) {
		this.displayCourses = COURSES_TO_START;
		initData();
	}

	@UiHandler("btnCoursesFinished")
	void handleClickFinished(ClickEvent e) {
		this.displayCourses = COURSES_FINISHED;
		initData();
	}

	private void refreshButtonsSelection() {
		btnCoursesAll.removeStyleName("btnSelected");
		btnCoursesInProgress.removeStyleName("btnSelected");
		btnCoursesToStart.removeStyleName("btnSelected");
		btnCoursesToAcquire.removeStyleName("btnSelected");
		btnCoursesFinished.removeStyleName("btnSelected");
		btnCoursesAll.addStyleName("btnNotSelected");
		btnCoursesInProgress.addStyleName("btnNotSelected");
		btnCoursesToStart.addStyleName("btnNotSelected");
		btnCoursesToAcquire.addStyleName("btnNotSelected");
		btnCoursesFinished.addStyleName("btnNotSelected");

		if (GenericWelcomeCoursesView.COURSES_ALL.equals(displayCourses)) {
			btnCoursesAll.addStyleName("btnSelected");
			btnCoursesAll.removeStyleName("btnNotSelected");
		} else if (COURSES_IN_PROGRESS.equals(displayCourses)) {
			btnCoursesInProgress.addStyleName("btnSelected");
			btnCoursesInProgress.removeStyleName("btnNotSelected");
		} else if (COURSES_TO_START.equals(displayCourses)) {
			btnCoursesToStart.addStyleName("btnSelected");
			btnCoursesToStart.removeStyleName("btnNotSelected");
		} else if (COURSES_TO_ACQUIRE.equals(displayCourses)) {
			btnCoursesToAcquire.addStyleName("btnSelected");
			btnCoursesToAcquire.removeStyleName("btnNotSelected");
		} else if (COURSES_FINISHED.equals(displayCourses)) {
			btnCoursesFinished.addStyleName("btnSelected");
			btnCoursesFinished.removeStyleName("btnNotSelected");
		}
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

}