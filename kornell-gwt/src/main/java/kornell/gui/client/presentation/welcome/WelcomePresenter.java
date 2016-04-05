package kornell.gui.client.presentation.welcome;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.EnrollmentProgressDescription;
import kornell.core.to.CourseClassTO;
import kornell.core.to.CourseClassesTO;
import kornell.gui.client.ClientFactory;
import kornell.gui.client.personnel.Student;
import kornell.gui.client.personnel.Teachers;
import kornell.gui.client.presentation.bar.MenuBarView;
import kornell.gui.client.presentation.welcome.summary.CourseSummaryPresenter;

import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class WelcomePresenter implements WelcomeView.Presenter{
	private WelcomeView view;
	private KornellSession session;
	private EventBus bus;
	private PlaceController placeCtrl;
	private MenuBarView menuBarView;

	public WelcomePresenter(ClientFactory clientFactory) {
		this.session = clientFactory.getKornellSession();
		this.bus = clientFactory.getEventBus();
		this.placeCtrl = clientFactory.getPlaceController();
		this.menuBarView = clientFactory.getViewFactory().getMenuBarView();
		view = clientFactory.getViewFactory().getWelcomeView();
		view.setPresenter(this);
		initData();
	}
	
	@Override
	public void initData() {
		session.courseClasses().getCourseClassesTO(new Callback<CourseClassesTO>() {
			@Override
			public void ok(CourseClassesTO courseClassesTO) {
				view.display(courseClassesTO, menuBarView);
			}
		});

		bus.addHandler(PlaceChangeEvent.TYPE, new PlaceChangeEvent.Handler() {
			@Override
			public void onPlaceChange(PlaceChangeEvent event) {
				if (event.getNewPlace() instanceof WelcomePlace) {
					initData();
				}
			}
		});
	}

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	@Override
	public boolean isEnrolled(CourseClassTO courseClassTO) {
		return getStudent(courseClassTO).isEnrolled();
	}
	
	@Override 
	public CourseSummaryPresenter getCourseSummaryPresenter(CourseClassTO courseClassTO){
		return new CourseSummaryPresenter(session, placeCtrl, courseClassTO, getStudent(courseClassTO));
	}
	
	private Student getStudent(CourseClassTO courseClassTO){
		return Teachers.of(courseClassTO).student(session.getCurrentUser());
	}

	@Override
	public EnrollmentProgressDescription getEnrollmentProgressDescription(CourseClassTO courseClassTO) {
		return getStudent(courseClassTO).getEnrollmentProgress().getDescription();
	}

}
