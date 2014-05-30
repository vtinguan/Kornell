package kornell.gui.client.presentation.welcome.generic;

import java.util.ArrayList;
import java.util.List;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.Enrollment;
import kornell.core.entity.EnrollmentProgressDescription;
import kornell.core.to.CourseClassTO;
import kornell.core.to.CourseClassesTO;
import kornell.core.to.EnrollmentTO;
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
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

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

	private KornellSession session;

	private PlaceController placeCtrl;

	private Button selectedFilterButton;

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
		
		selectedFilterButton = btnCoursesAll;
		initData();
	}

	private void initData() {
		session.getCourseClassesTOByInstitution(Dean.getInstance()
				.getInstitution().getUUID(), new Callback<CourseClassesTO>() {
			@Override
			public void ok(CourseClassesTO tos) {
				Dean.getInstance().setCourseClassesTO(tos);
				display(tos);
			}
		});
	}

	private void display(final CourseClassesTO tos) {
		updateUserEnrollments(tos);
		
		pnlCourses.clear();
		final int classesCount = tos.getCourseClasses().size();
		if(classesCount == 0){
			coursesPanel.setVisible(false);
			KornellNotification.show("Você não está matriculado em uma turma e não há turmas disponíveis para solicitar uma nova matrícula.", AlertType.INFO, 8000);
		} else {
			coursesPanel.setVisible(true);
		}
		
		prepareButtons(classesCount);
		prepareClassesPanel(tos);
	}

	private void prepareClassesPanel(final CourseClassesTO tos) {
		final int classesCount = tos.getCourseClasses().size();
		
	  for (final CourseClassTO courseClassTO : tos.getCourseClasses()) {
			final Teacher teacher = Teachers.of(courseClassTO);

			session.getCurrentUser(new Callback<UserInfoTO>() {
				@Override
				public void ok(UserInfoTO userInfoTO) {
					Student student = teacher.student(userInfoTO);
					addPanelIfFiltered(btnCoursesAll, courseClassTO);
					if (student.isEnrolled()) {
						EnrollmentProgressDescription description = student.getEnrollmentProgress().getDescription();
						switch (description) {
						case completed:
							addPanelIfFiltered(btnCoursesFinished, courseClassTO);
							break;
						case inProgress:
							addPanelIfFiltered(btnCoursesInProgress, courseClassTO);
							break;
						case notStarted:
							addPanelIfFiltered(btnCoursesToStart, courseClassTO);
							break;
						}
					} else {
						addPanelIfFiltered(btnCoursesToAcquire, courseClassTO);
					}
				}
				
				private void addPanelIfFiltered(Button button, CourseClassTO courseClassTO){
					if(button.equals(selectedFilterButton)){
						pnlCourses.add(new GenericCourseSummaryView(placeCtrl, courseClassTO, session));
					}
					if(classesCount > 1)
						button.setVisible(true);
				}
			});
		}
  }

	private void updateUserEnrollments(CourseClassesTO tos) {
		EnrollmentsTO enrollmentsTO = toFactory.newEnrollmentsTO().as();
		List<EnrollmentTO> enrollmentTOs = new ArrayList<EnrollmentTO>();
		EnrollmentTO enrollmentTO;
		for (CourseClassTO courseClassTO : tos.getCourseClasses()) {
			if (courseClassTO.getEnrollment() != null) {
				enrollmentTO = toFactory.newEnrollmentTO().as();
				enrollmentTO.setEnrollment(courseClassTO.getEnrollment());
				enrollmentTO.setPerson(session.getCurrentUser().getPerson());
				enrollmentTOs.add(enrollmentTO);
			}
		}
		enrollmentsTO.setEnrollmentTOs(enrollmentTOs);
		session.getCurrentUser().setEnrollmentsTO(enrollmentsTO);
	}

	private void prepareButtons(int classesCount) {
		refreshButtonSelection(btnCoursesAll);
		refreshButtonSelection(btnCoursesInProgress);
		refreshButtonSelection(btnCoursesToStart);
		refreshButtonSelection(btnCoursesToAcquire);
		refreshButtonSelection(btnCoursesFinished);
  }


	@UiHandler(value={"btnCoursesAll", "btnCoursesInProgress", "btnCoursesToAcquire", "btnCoursesToStart", "btnCoursesFinished"})
	void handleClickAll(ClickEvent e) {
		this.selectedFilterButton = (Button) e.getSource();
		initData();
	}
	
	private void refreshButtonSelection(Button button){
		button.setVisible(false);
		button.removeStyleName("btnSelected");
		button.addStyleName("btnNotSelected");
		if (selectedFilterButton != null && selectedFilterButton.equals(button)) {
			button.addStyleName("btnSelected");
			button.removeStyleName("btnNotSelected");
		}
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

}