package kornell.gui.client;

import kornell.api.client.KornellClient;
import kornell.gui.client.content.SequencerFactory;
import kornell.gui.client.content.SequencerFactoryImpl;
import kornell.gui.client.presentation.GlobalActivityMapper;
import kornell.gui.client.presentation.HistoryMapper;
import kornell.gui.client.presentation.atividade.AtividadePlace;
import kornell.gui.client.presentation.atividade.AtividadePresenter;
import kornell.gui.client.presentation.atividade.AtividadeView;
import kornell.gui.client.presentation.atividade.generic.GenericAtividadeView;
import kornell.gui.client.presentation.bar.ActivityBarView;
import kornell.gui.client.presentation.bar.CourseBarView;
import kornell.gui.client.presentation.bar.MenuBarView;
import kornell.gui.client.presentation.bar.SouthBarView;
import kornell.gui.client.presentation.bar.generic.GenericActivityBarView;
import kornell.gui.client.presentation.bar.generic.GenericCourseBarView;
import kornell.gui.client.presentation.bar.generic.GenericMenuBarView;
import kornell.gui.client.presentation.bar.generic.GenericSouthBarView;
import kornell.gui.client.presentation.course.CoursePlace;
import kornell.gui.client.presentation.course.CoursePresenter;
import kornell.gui.client.presentation.course.CourseView;
import kornell.gui.client.presentation.course.generic.GenericCourseView;
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
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
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
	private final KornellClient client = KornellClient.getInstance();

	/* Views */
	private GenericMenuBarView menuBarView;
	private SouthBarView southBarView;
	
	private GenericHomeView genericHomeView;
	private AtividadePresenter activityPresenter;
	private CoursePresenter coursePresenter;

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

	private void initHistoryHandler() {
		historyHandler.register(placeController, bus, new VitrinePlace());
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
		initGUI();
		initActivityManagers();
		initHistoryHandler();
		initException();
		initSCORM();
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
		return new GenericVitrineView(placeController, client);
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
	public CourseView getCourseView() {
		return new GenericCourseView(bus, client, placeController);
	}
	
	@Override
	public CoursePresenter getCoursePresenter() {
		SequencerFactory rendererFactory = new SequencerFactoryImpl(bus,placeController,client);
		if (coursePresenter == null) {
			CourseView courseView = getCourseView();
			
			coursePresenter = new CoursePresenter(courseView, placeController);
		}
		return coursePresenter;
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

}
