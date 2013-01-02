package kornell.client;

import kornell.api.client.KornellClient;
import kornell.client.activity.GlobalActivityMapper;
import kornell.client.activity.GlobalPlaceHistoryMapper;
import kornell.client.presenter.home.HomeView;
import kornell.client.presenter.vitrine.VitrineView;
import kornell.client.presenter.welcome.WelcomeView;
import kornell.client.view.generic.GenericHomeView;
import kornell.client.view.generic.GenericVitrineView;
import kornell.client.view.generic.GenericWelcomeView;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

public class GenericClientFactoryImpl implements ClientFactory {
	private final EventBus eventBus = new SimpleEventBus();
	private final UserSession userSession = new UserSession();
	private final PlaceController placeController = new PlaceController(eventBus);
	
	private final GlobalPlaceHistoryMapper globalHistoryMapper = GWT
			.create(GlobalPlaceHistoryMapper.class);
	private final PlaceHistoryHandler globalHistoryHandler = new PlaceHistoryHandler(
			globalHistoryMapper);
	private ActivityManager globalActivityManager;
	
	
	private final String apiURL = "http://api.kornell.localdomain:8080";
	private final KornellClient client = new KornellClient(apiURL);


	public GenericClientFactoryImpl() {
		System.out.println("GenericClientFactoryImpl()");
	}

	@Override
	public App getApp() {
		return new App(eventBus, placeController,
				getActivityManager(),
				globalHistoryMapper, globalHistoryHandler);
	}

	protected ActivityManager getActivityManager() {
		if (globalActivityManager == null) {
			globalActivityManager = 
					new ActivityManager(createActivityMapper(),
					eventBus);
		}
		return globalActivityManager;
	}
	
	protected ActivityMapper createActivityMapper() {
		    return new GlobalActivityMapper(this);
	}

	@Override
	public HomeView getHomeView() {
		return new GenericHomeView(this, eventBus,placeController,globalHistoryHandler, client);
	}

	@Override
	public VitrineView getVitrineView() {
		return new GenericVitrineView(placeController,client);
	}

	@Override
	public UserSession getUserSession() {
		return userSession;
	}

	@Override
	public WelcomeView getWelcomeView() {
		return new GenericWelcomeView();
	}

}
