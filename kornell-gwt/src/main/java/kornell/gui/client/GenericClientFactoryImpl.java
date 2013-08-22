package kornell.gui.client;

import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.core.shared.to.UserInfoTO;
import kornell.gui.client.content.SequencerFactory;
import kornell.gui.client.content.SequencerFactoryImpl;
import kornell.gui.client.presentation.GlobalActivityMapper;
import kornell.gui.client.presentation.HistoryMapper;
import kornell.gui.client.presentation.atividade.AtividadePlace;
import kornell.gui.client.presentation.atividade.AtividadePresenter;
import kornell.gui.client.presentation.atividade.AtividadeView;
import kornell.gui.client.presentation.atividade.generic.GenericAtividadeView;
import kornell.gui.client.presentation.bar.MenuBarView;
import kornell.gui.client.presentation.bar.SouthBarView;
import kornell.gui.client.presentation.bar.generic.GenericMenuBarView;
import kornell.gui.client.presentation.bar.generic.GenericSouthBarView;
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
import kornell.gui.client.presentation.course.notes.CourseNotesPresenter;
import kornell.gui.client.presentation.course.notes.CourseNotesView;
import kornell.gui.client.presentation.course.notes.generic.GenericCourseNotesView;
import kornell.gui.client.presentation.course.specialists.CourseSpecialistsPresenter;
import kornell.gui.client.presentation.course.specialists.CourseSpecialistsView;
import kornell.gui.client.presentation.course.specialists.generic.GenericCourseSpecialistsView;
import kornell.gui.client.presentation.home.HomePlace;
import kornell.gui.client.presentation.home.HomeView;
import kornell.gui.client.presentation.home.generic.GenericHomeView;
import kornell.gui.client.presentation.terms.TermsView;
import kornell.gui.client.presentation.terms.generic.GenericTermsView;
import kornell.gui.client.presentation.vitrine.VitrinePlace;
import kornell.gui.client.presentation.vitrine.VitrineView;
import kornell.gui.client.presentation.vitrine.generic.GenericVitrineView;
import kornell.gui.client.presentation.welcome.WelcomeView;
import kornell.gui.client.presentation.welcome.generic.GenericWelcomeView;
import kornell.gui.client.scorm.API_1484_11;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

public class GenericClientFactoryImpl implements ClientFactory {
	/* History Management */
	private final EventBus bus = new SimpleEventBus();
	private final PlaceController placeController = new PlaceController(
			bus);
	private final HistoryMapper historyMapper = GWT.create(HistoryMapper.class);
	private final PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(
			historyMapper);

	/* Activity Managers */
	private ActivityManager globalActivityManager;

	private SimplePanel appPanel;

	/* REST API Client */
	private static final KornellClient client = KornellClient.getInstance();

	/* Views */
	private GenericMenuBarView menuBarView;
	private SouthBarView southBarView;
	
	private GenericHomeView genericHomeView;
	private AtividadePresenter activityPresenter;
	private CourseHomePresenter courseHomePresenter;
	private CourseDetailsPresenter courseDetailsPresenter;
	private CourseLibraryPresenter courseLibraryPresenter;
	private CourseForumPresenter courseForumPresenter;
	private CourseChatPresenter courseChatPresenter;
	private CourseSpecialistsPresenter courseSpecialistsPresenter;
	private CourseNotesPresenter courseNotesPresenter;

	/* GUI */
	SimplePanel shell = new SimplePanel();

	public GenericClientFactoryImpl() {
	}

	private void initActivityManagers() {
		initGlobalActivityManager();
	}

	private void initGlobalActivityManager() {
		globalActivityManager = new ActivityManager(new GlobalActivityMapper(
				this), bus);
		globalActivityManager.setDisplay(shell);
	}

	private void initHistoryHandler(Place defaultPlace) {
		historyHandler.register(placeController, bus, defaultPlace);
		historyHandler.handleCurrentHistory();
	}

	private void initGUI() {
		final RootLayoutPanel rootLayoutPanel = RootLayoutPanel.get();
		final DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		dockLayoutPanel.addNorth(getMenuBarView(), 45);
		dockLayoutPanel.addSouth(getSouthBarView(), 35);
		
		ScrollPanel sp = new ScrollPanel();
		sp.add(shell);
		dockLayoutPanel.add(sp);
		dockLayoutPanel.addStyleName("wrapper");
		rootLayoutPanel.add(dockLayoutPanel);

		bus.addHandler(PlaceChangeEvent.TYPE,
				new PlaceChangeEvent.Handler() {
					@Override
					public void onPlaceChange(PlaceChangeEvent event) {
						setPlaceNameAsBodyStyle(event);
						
						Place newPlace = event.getNewPlace();
						dockLayoutPanel.setWidgetHidden((Widget) getSouthBarView(), !getSouthBarView().isVisible());
					}

					private void setPlaceNameAsBodyStyle(PlaceChangeEvent event) {
						String styleName = rootLayoutPanel.getStyleName();
						if (!styleName.isEmpty())
							rootLayoutPanel.removeStyleName(styleName);
						String[] split = event.getNewPlace().getClass().getName().split("\\.");
						String newStyle = split[split.length - 1];
						rootLayoutPanel.addStyleName(newStyle);
					}
				});

	}

	private MenuBarView getMenuBarView() {
		if (menuBarView == null)
			menuBarView = new GenericMenuBarView(placeController,bus);
		return menuBarView;
	}
	
	private SouthBarView getSouthBarView() {
		if (southBarView == null)
			southBarView = new GenericSouthBarView(bus, placeController);
		return southBarView;
	}

	@Override
	public ClientFactory startApp() {
		//TODO: Consider caching credentials to avoid this request
		client.getCurrentUser(new Callback<UserInfoTO>(){
			@Override
			protected void ok(UserInfoTO user) {
				GWT.log("### Activity "+user.getPerson().getFullName());
//				HomePlace defaultPlace = new HomePlace();
				Place defaultPlace = getDefaultPlace();
				startApp(defaultPlace);
			}

			@Override
			protected void unauthorized() {
				GWT.log("### Unauthorized");
				startApp(new VitrinePlace());
			}
			
			protected void startApp(Place defaultPlace){
				initGUI();
				initActivityManagers();
				initHistoryHandler(defaultPlace);
				initException();
				initSCORM();
			}			
		});
		return this;
	}

	private void initSCORM() {
		new API_1484_11(bus).bindToWindow();
	}

	private void initException() {
		GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {

			@Override
			public void onUncaughtException(Throwable e) {
				System.out.println("** UNCAUGHT **");
				e.printStackTrace();
			}
		});
	}

	@Override
	public HomeView getHomeView() {
		if (genericHomeView == null) {
			genericHomeView = new GenericHomeView(this, bus,
					historyHandler, client, appPanel);
		}
		return genericHomeView;
	}

	@Override
	public VitrineView getVitrineView() {
		return new GenericVitrineView(placeController, getDefaultPlace(),client);
	}

	@Override
	public WelcomeView getWelcomeView() {
		return new GenericWelcomeView(bus, client, placeController);
	}

	@Override
	public AtividadeView getActivityView() {
		return new GenericAtividadeView(bus);
	}

	@Override
	public TermsView getTermsView() {
		return new GenericTermsView(client, placeController);
	}
	
	
	
	@Override
	public CourseHomePresenter getCourseHomePresenter() {
		if (courseHomePresenter == null) {
			CourseHomeView courseHomeView = getCourseHomeView();
			
			courseHomePresenter = new CourseHomePresenter(courseHomeView, placeController);
		}
		return courseHomePresenter;
	}
	@Override
	public CourseHomeView getCourseHomeView() {
		return new GenericCourseHomeView(bus, client, placeController);
	}
	
	
	
	@Override
	public CourseDetailsPresenter getCourseDetailsPresenter() {
		if (courseDetailsPresenter == null) {
			CourseDetailsView courseDetailsView = getCourseDetailsView();
			
			courseDetailsPresenter = new CourseDetailsPresenter(courseDetailsView, placeController);
		}
		return courseDetailsPresenter;
	}
	@Override
	public CourseDetailsView getCourseDetailsView() {
		return new GenericCourseDetailsView(bus, client, placeController);
	}
	
	
	
	@Override
	public CourseLibraryPresenter getCourseLibraryPresenter() {
		if (courseLibraryPresenter == null) {
			CourseLibraryView courseLibraryView = getCourseLibraryView();
			
			courseLibraryPresenter = new CourseLibraryPresenter(courseLibraryView, placeController);
		}
		return courseLibraryPresenter;
	}
	@Override
	public CourseLibraryView getCourseLibraryView() {
		return new GenericCourseLibraryView(bus, client, placeController);
	}
	
	
	
	@Override
	public CourseForumPresenter getCourseForumPresenter() {
		if (courseForumPresenter == null) {
			CourseForumView courseForumView = getCourseForumView();
			
			courseForumPresenter = new CourseForumPresenter(courseForumView, placeController);
		}
		return courseForumPresenter;
	}
	@Override
	public CourseForumView getCourseForumView() {
		return new GenericCourseForumView(bus, client, placeController);
	}
	
	
	
	@Override
	public CourseChatPresenter getCourseChatPresenter() {
		if (courseChatPresenter == null) {
			CourseChatView courseChatView = getCourseChatView();
			
			courseChatPresenter = new CourseChatPresenter(courseChatView, placeController);
		}
		return courseChatPresenter;
	}
	@Override
	public CourseChatView getCourseChatView() {
		return new GenericCourseChatView(bus, client, placeController);
	}
	
	
	
	@Override
	public CourseSpecialistsPresenter getCourseSpecialistsPresenter() {
		if (courseSpecialistsPresenter == null) {
			CourseSpecialistsView courseSpecialistsView = getCourseSpecialistsView();
			
			courseSpecialistsPresenter = new CourseSpecialistsPresenter(courseSpecialistsView, placeController);
		}
		return courseSpecialistsPresenter;
	}
	@Override
	public CourseSpecialistsView getCourseSpecialistsView() {
		return new GenericCourseSpecialistsView(bus, client, placeController);
	}
	
	
	
	@Override
	public CourseNotesPresenter getCourseNotesPresenter() {
		if (courseNotesPresenter == null) {
			CourseNotesView courseNotesView = getCourseNotesView();
			
			courseNotesPresenter = new CourseNotesPresenter(courseNotesView, placeController);
		}
		return courseNotesPresenter;
	}
	@Override
	public CourseNotesView getCourseNotesView() {
		return new GenericCourseNotesView(bus, client, placeController);
	}
	
	
	
	@Override
	public AtividadePresenter getActivityPresenter() {
		SequencerFactory rendererFactory = new SequencerFactoryImpl(bus,placeController,client);
		if (activityPresenter == null) {
			AtividadeView activityView = getActivityView();
			
			activityPresenter = new AtividadePresenter(activityView,
					placeController, rendererFactory);
		}
		return activityPresenter;
	}
	
	static final AtividadePlace DEFAULT_PLACE = new AtividadePlace("d9aaa03a-f225-48b9-8cc9-15495606ac46", 0);
	public Place getDefaultPlace() {
		
		return  DEFAULT_PLACE;
	}
	
}
