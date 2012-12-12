package kornell.client;

import kornell.client.activity.AppActivityMapper;
import kornell.client.activity.AppPlaceHistoryMapper;
import kornell.client.presenter.home.HomeView;
import kornell.client.presenter.vitrine.VitrineView;
import kornell.client.view.generic.GenericHomeView;
import kornell.client.view.generic.GenericVitrineView;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

public class DesktopClientFactoryImpl implements ClientFactory {
	private final EventBus eventBus = new SimpleEventBus();
	private final UserSession userSession = new UserSession();
	private final PlaceController placeController = new PlaceController(
			eventBus);
	private final AppPlaceHistoryMapper historyMapper = GWT
			.create(AppPlaceHistoryMapper.class);
	private final PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(
			historyMapper);
	private ActivityManager activityManager;


	public DesktopClientFactoryImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public App getApp() {
		return new App(eventBus, placeController,
				getActivityManager(),
				historyMapper, historyHandler);
	}

	protected ActivityManager getActivityManager() {
		if (activityManager == null) {
			activityManager = 
					new ActivityManager(createActivityMapper(),
					eventBus);
		}
		return activityManager;
	}
	
	protected ActivityMapper createActivityMapper() {
		    return new AppActivityMapper(this);
	}

	@Override
	public HomeView getHomeView() {
		return new GenericHomeView(placeController);
	}

	@Override
	public VitrineView getVitrineView() {
		return new GenericVitrineView(placeController);
	}

	@Override
	public UserSession getUserSession() {
		return userSession;
	}

}
