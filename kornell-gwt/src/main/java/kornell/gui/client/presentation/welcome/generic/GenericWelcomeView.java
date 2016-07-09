package kornell.gui.client.presentation.welcome.generic;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import kornell.core.entity.EnrollmentProgressDescription;
import kornell.core.to.CourseClassTO;
import kornell.core.to.CourseClassesTO;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.presentation.bar.MenuBarView;
import kornell.gui.client.presentation.welcome.WelcomeView;
import kornell.gui.client.util.view.KornellNotification;

public class GenericWelcomeView extends Composite implements WelcomeView {
	interface MyUiBinder extends UiBinder<Widget, GenericWelcomeView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	FlowPanel coursesWrapper;
	@UiField
	FlowPanel pnlCourses;
	
	Button btnCoursesAll, btnCoursesInProgress, btnCoursesToStart, btnCoursesToAcquire, btnCoursesFinished;

	private static KornellConstants constants = GWT.create(KornellConstants.class);
	private Button selectedFilterButton;
	private ArrayList<IsWidget> widgets;

	private int classesCount;

	private Presenter presenter;

	public GenericWelcomeView() {
		initWidget(uiBinder.createAndBindUi(this));
		coursesWrapper.setVisible(false);
	}

	private void startPlaceBar(MenuBarView menuBarView) {
		menuBarView.initPlaceBar(IconType.HOME, constants.homeTitle(), constants.homeDescription());
		if (widgets == null) {
			widgets = new ArrayList<IsWidget>();
			btnCoursesFinished = startButton(constants.finished(), widgets);
			btnCoursesToAcquire = startButton("Disponíveis", widgets);
			btnCoursesToStart = startButton(constants.toStart(), widgets);
			btnCoursesInProgress = startButton(constants.inProgress(), widgets);
			btnCoursesAll = startButton(constants.allClasses(), widgets);
			selectedFilterButton = btnCoursesAll;
		}
		menuBarView.setPlaceBarWidgets(widgets);
	}

	private Button startButton(String text, List<IsWidget> widgets) {
		Button button = new Button(text);
		button.addStyleName("btnPlaceBar");
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				selectedFilterButton = (Button) event.getSource();
				presenter.initData();
			}
		});
		widgets.add(button);
		return button;
	}
	
	@Override
	public void display(final CourseClassesTO tos, MenuBarView menuBarView) {
		startPlaceBar(menuBarView);
		pnlCourses.clear();
		final int classesCount = tos.getCourseClasses().size();
		if (classesCount == 0) {
			coursesWrapper.setVisible(false);
			KornellNotification.show(constants.noClassesAvailable(), AlertType.WARNING, 8000);
		} else {
			coursesWrapper.setVisible(true);
		}

		prepareButtons(classesCount);
		prepareClassesPanel(tos);
	}

	private void prepareClassesPanel(final CourseClassesTO tos) {
		classesCount = tos.getCourseClasses().size();

		for (final CourseClassTO courseClassTO : tos.getCourseClasses()) {
			if (courseClassTO.getCourseClass().isInvisible())
				continue;
			addPanelIfFiltered(btnCoursesAll, courseClassTO);
			if (presenter.isEnrolled(courseClassTO)) {
				EnrollmentProgressDescription description = presenter.getEnrollmentProgressDescription(courseClassTO);
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
	}

	private void addPanelIfFiltered(Button button, CourseClassTO courseClassTO) {
		if (button.equals(selectedFilterButton)) {
			pnlCourses.add(presenter.getCourseSummaryPresenter(courseClassTO).asWidget());
		}
		if (classesCount > 3)
			button.setVisible(true);
	}

	private void prepareButtons(int classesCount) {
		refreshButtonSelection(btnCoursesAll);
		refreshButtonSelection(btnCoursesInProgress);
		refreshButtonSelection(btnCoursesToStart);
		refreshButtonSelection(btnCoursesToAcquire);
		refreshButtonSelection(btnCoursesFinished);
	}

	private void refreshButtonSelection(Button button) {
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