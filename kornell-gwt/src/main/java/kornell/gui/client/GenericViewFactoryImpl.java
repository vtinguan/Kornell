package kornell.gui.client;

import kornell.gui.client.presentation.admin.home.AdminHomeView;
import kornell.gui.client.presentation.admin.home.generic.GenericAdminHomeView;
import kornell.gui.client.presentation.atividade.generic.GenericClassroomView;
import kornell.gui.client.presentation.bar.MenuBarView;
import kornell.gui.client.presentation.bar.SouthBarView;
import kornell.gui.client.presentation.bar.generic.GenericMenuBarView;
import kornell.gui.client.presentation.bar.generic.GenericSouthBarView;
import kornell.gui.client.presentation.course.ClassroomPresenter;
import kornell.gui.client.presentation.course.ClassroomView;
import kornell.gui.client.presentation.course.chat.CourseChatPresenter;
import kornell.gui.client.presentation.course.chat.CourseChatView;
import kornell.gui.client.presentation.course.chat.generic.GenericCourseChatView;
import kornell.gui.client.presentation.course.course.CourseHomePresenter;
import kornell.gui.client.presentation.course.course.CourseHomeView;
import kornell.gui.client.presentation.course.course.generic.GenericCourseHomeView;
import kornell.gui.client.presentation.course.details.CourseDetailsPresenter;
import kornell.gui.client.presentation.course.details.CourseDetailsView;
import kornell.gui.client.presentation.course.details.generic.GenericCourseDetailsView;
import kornell.gui.client.presentation.course.forum.CourseForumPresenter;
import kornell.gui.client.presentation.course.forum.CourseForumView;
import kornell.gui.client.presentation.course.forum.generic.GenericCourseForumView;
import kornell.gui.client.presentation.course.library.CourseLibraryPresenter;
import kornell.gui.client.presentation.course.library.CourseLibraryView;
import kornell.gui.client.presentation.course.library.generic.GenericCourseLibraryView;
import kornell.gui.client.presentation.course.specialists.CourseSpecialistsPresenter;
import kornell.gui.client.presentation.course.specialists.CourseSpecialistsView;
import kornell.gui.client.presentation.course.specialists.generic.GenericCourseSpecialistsView;
import kornell.gui.client.presentation.home.HomeView;
import kornell.gui.client.presentation.home.generic.GenericHomeView;
import kornell.gui.client.presentation.profile.ProfileView;
import kornell.gui.client.presentation.profile.generic.GenericProfileView;
import kornell.gui.client.presentation.sandbox.SandboxPresenter;
import kornell.gui.client.presentation.sandbox.SandboxView;
import kornell.gui.client.presentation.sandbox.generic.GenericSandboxView;
import kornell.gui.client.presentation.terms.TermsView;
import kornell.gui.client.presentation.terms.generic.GenericTermsView;
import kornell.gui.client.presentation.vitrine.VitrinePlace;
import kornell.gui.client.presentation.vitrine.VitrineView;
import kornell.gui.client.presentation.vitrine.generic.GenericVitrineView;
import kornell.gui.client.presentation.welcome.WelcomeView;
import kornell.gui.client.presentation.welcome.generic.GenericWelcomeView;
import kornell.gui.client.sequence.SequencerFactory;
import kornell.gui.client.sequence.SequencerFactoryImpl;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

//TODO: Organize this big, messy class and interface
public class GenericViewFactoryImpl implements ViewFactory {

	private ClientFactory clientFactory;
	
	/* Views */
	private GenericMenuBarView menuBarView;
	private SouthBarView southBarView;
	private GenericHomeView genericHomeView;
	private ClassroomPresenter coursePresenter;
	private CourseHomePresenter courseHomePresenter;
	private CourseDetailsPresenter courseDetailsPresenter;
	private CourseLibraryPresenter courseLibraryPresenter;
	private CourseForumPresenter courseForumPresenter;
	private CourseChatPresenter courseChatPresenter;
	private CourseSpecialistsPresenter courseSpecialistsPresenter;
	private SandboxPresenter sandboxPresenter;
	
	private SimplePanel appPanel;
	
	SimplePanel shell = new SimplePanel();

	public GenericViewFactoryImpl(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}
	
	@Override
	public void initGUI() {
		final RootLayoutPanel rootLayoutPanel = RootLayoutPanel.get();
		final DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		dockLayoutPanel.addNorth(getMenuBarView(), 45);
		dockLayoutPanel.addSouth(getSouthBarView(), 35);

		ScrollPanel sp = new ScrollPanel();
		sp.add(shell);
		dockLayoutPanel.add(sp);
		sp.addStyleName("vScrollBar");
		dockLayoutPanel.addStyleName("wrapper");
		rootLayoutPanel.add(dockLayoutPanel);

		clientFactory.getEventBus().addHandler(PlaceChangeEvent.TYPE, new PlaceChangeEvent.Handler() {
			@Override
			public void onPlaceChange(PlaceChangeEvent event) {
				setPlaceNameAsBodyStyle(event);
				dockLayoutPanel.setWidgetHidden((Widget) getSouthBarView(),
						!getSouthBarView().isVisible());
				if (clientFactory.getPlaceController().getWhere() instanceof VitrinePlace) {
					dockLayoutPanel.setWidgetSize(getMenuBarView().asWidget(),
							0);
				} else {
					dockLayoutPanel.setWidgetSize(getMenuBarView().asWidget(),
							45);
					getMenuBarView().display();
				}
			}

			private void setPlaceNameAsBodyStyle(PlaceChangeEvent event) {
				String styleName = rootLayoutPanel.getStyleName();
				if (!styleName.isEmpty())
					rootLayoutPanel.removeStyleName(styleName);
				String[] split = event.getNewPlace().getClass().getName()
						.split("\\.");
				String newStyle = split[split.length - 1];
				rootLayoutPanel.addStyleName(newStyle);
			}
		});
	}

	@Override
	public MenuBarView getMenuBarView() {
		if (menuBarView == null)
			menuBarView = new GenericMenuBarView(clientFactory);
		return menuBarView;
	}

	@Override
	public SouthBarView getSouthBarView() {
		if (southBarView == null)
			southBarView = new GenericSouthBarView(clientFactory);
		return southBarView;
	}

	@Override
	public HomeView getHomeView() {
		if (genericHomeView == null) {
			genericHomeView = new GenericHomeView(clientFactory, clientFactory.getEventBus(), clientFactory.getUserSession(), appPanel);
		}
		return genericHomeView;
	}

	@Override
	public VitrineView getVitrineView() {
		return new GenericVitrineView();
	}

	@Override
	public WelcomeView getWelcomeView() {
		return new GenericWelcomeView(clientFactory.getEventBus(), clientFactory.getUserSession(), clientFactory.getPlaceController());
	}

	@Override
	public ProfileView getProfileView() {
		return new GenericProfileView(clientFactory);
	}

	@Override
	public ClassroomView getClassroomView() {
		return new GenericClassroomView(clientFactory.getPlaceController(), clientFactory.getUserSession(), clientFactory.getEventBus());
	}

	@Override
	public TermsView getTermsView() {
		return new GenericTermsView(clientFactory);
	}

	@Override
	public CourseHomePresenter getCourseHomePresenter() {
		if (courseHomePresenter == null) {
			CourseHomeView courseHomeView = getCourseHomeView();

			courseHomePresenter = new CourseHomePresenter(courseHomeView,
					clientFactory.getPlaceController());
		}
		return courseHomePresenter;
	}

	@Override
	public CourseHomeView getCourseHomeView() {
		return new GenericCourseHomeView(clientFactory.getEventBus(), clientFactory.getUserSession(), clientFactory.getPlaceController());
	}

	@Override
	public CourseLibraryPresenter getCourseLibraryPresenter() {
		if (courseLibraryPresenter == null) {
			CourseLibraryView courseLibraryView = getCourseLibraryView();

			courseLibraryPresenter = new CourseLibraryPresenter(
					courseLibraryView, clientFactory.getPlaceController());
		}
		return courseLibraryPresenter;
	}

	@Override
	public CourseLibraryView getCourseLibraryView() {
		return new GenericCourseLibraryView(clientFactory.getEventBus(), clientFactory.getUserSession(), clientFactory.getPlaceController());
	}

	@Override
	public CourseForumPresenter getCourseForumPresenter() {
		if (courseForumPresenter == null) {
			CourseForumView courseForumView = getCourseForumView();

			courseForumPresenter = new CourseForumPresenter(courseForumView,
					clientFactory.getPlaceController());
		}
		return courseForumPresenter;
	}

	@Override
	public CourseForumView getCourseForumView() {
		return new GenericCourseForumView(clientFactory.getEventBus(), clientFactory.getUserSession(), clientFactory.getPlaceController());
	}

	@Override
	public CourseChatPresenter getCourseChatPresenter() {
		if (courseChatPresenter == null) {
			CourseChatView courseChatView = getCourseChatView();
			courseChatPresenter = new CourseChatPresenter(courseChatView,
					clientFactory.getPlaceController());
		}
		return courseChatPresenter;
	}

	@Override
	public CourseChatView getCourseChatView() {
		return new GenericCourseChatView(clientFactory.getEventBus(), clientFactory.getUserSession(), clientFactory.getPlaceController());
	}

	@Override
	public CourseSpecialistsPresenter getCourseSpecialistsPresenter() {
		if (courseSpecialistsPresenter == null) {
			CourseSpecialistsView courseSpecialistsView = getCourseSpecialistsView();

			courseSpecialistsPresenter = new CourseSpecialistsPresenter(
					courseSpecialistsView, clientFactory.getPlaceController());
		}
		return courseSpecialistsPresenter;
	}

	@Override
	public CourseSpecialistsView getCourseSpecialistsView() {
		return new GenericCourseSpecialistsView(clientFactory.getEventBus(), clientFactory.getUserSession(), clientFactory.getPlaceController());
	}

	@Override
	public ClassroomPresenter getClassroomPresenter() {
		SequencerFactory rendererFactory = new SequencerFactoryImpl(clientFactory.getEventBus(),
				clientFactory.getPlaceController(), clientFactory.getUserSession());
		if (coursePresenter == null) {
			ClassroomView activityView = getClassroomView();
			coursePresenter = new ClassroomPresenter(activityView, clientFactory.getPlaceController(),
					rendererFactory, clientFactory.getUserSession(), clientFactory.getEventBus());
		}
		return coursePresenter;
	}

	@Override
	public SandboxView getSandboxView() {
		return new GenericSandboxView();
	}

	@Override
	public SandboxPresenter getSandboxPresenter() {
		if (sandboxPresenter == null) {
			SandboxView sandboxView = getSandboxView();
			sandboxPresenter = new SandboxPresenter(sandboxView);
		}
		return sandboxPresenter;
	}

	// dean
	@Override
	public AdminHomeView getDeanHomeView() {
		return new GenericAdminHomeView();
	}

	@Override
	public SimplePanel getShell() {
		return shell;
	}
}
