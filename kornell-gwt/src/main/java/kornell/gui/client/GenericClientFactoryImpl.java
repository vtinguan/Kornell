package kornell.gui.client;

import kornell.api.client.KornellClient;
import kornell.gui.client.presentation.GlobalActivityMapper;
import kornell.gui.client.presentation.GlobalPlaceHistoryMapper;
import kornell.gui.client.presentation.home.HomeView;
import kornell.gui.client.presentation.home.generic.GenericHomeView;
import kornell.gui.client.presentation.vitrine.VitrineView;
import kornell.gui.client.presentation.vitrine.generic.GenericVitrineView;
import kornell.gui.client.presentation.welcome.WelcomeView;
import kornell.gui.client.presentation.welcome.generic.GenericWelcomeView;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

public class GenericClientFactoryImpl implements ClientFactory {
	private final EventBus eventBus = new SimpleEventBus();
	private final PlaceController placeController = new PlaceController(eventBus);
	
	private final GlobalPlaceHistoryMapper globalHistoryMapper = GWT
			.create(GlobalPlaceHistoryMapper.class);
	private final PlaceHistoryHandler globalHistoryHandler = new PlaceHistoryHandler(
			globalHistoryMapper);
	private ActivityManager globalActivityManager;
	
	
	private final String apiURL = "http://api.kornell.localdomain:8080";
	private final KornellClient client = new KornellClient(apiURL);
	GenericHomeView genericHomeView = new GenericHomeView(this, eventBus,placeController,globalHistoryHandler, client);


	public GenericClientFactoryImpl() {
		System.out.println("GenericClientFactoryImpl()");
	}

	@Override
	public App getApp() {
		return new App(eventBus, placeController,
				getGlobalActivityManager(),
				 globalHistoryHandler);
	}

	public ActivityManager getGlobalActivityManager() {
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
		return genericHomeView;
	}

	@Override
	public VitrineView getVitrineView() {
		return new GenericVitrineView(placeController,client);
	}

	@Override
	public WelcomeView getWelcomeView() {
		return new GenericWelcomeView();
	}

}
