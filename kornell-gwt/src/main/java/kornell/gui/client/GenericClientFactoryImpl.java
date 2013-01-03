package kornell.gui.client;

import kornell.api.client.KornellClient;
import kornell.gui.client.presentation.AppActivityMapper;
import kornell.gui.client.presentation.GlobalActivityMapper;
import kornell.gui.client.presentation.HistoryMapper;
import kornell.gui.client.presentation.home.HomeView;
import kornell.gui.client.presentation.home.generic.GenericHomeView;
import kornell.gui.client.presentation.vitrine.VitrinePlace;
import kornell.gui.client.presentation.vitrine.VitrineView;
import kornell.gui.client.presentation.vitrine.generic.GenericVitrineView;
import kornell.gui.client.presentation.welcome.WelcomeView;
import kornell.gui.client.presentation.welcome.generic.GenericWelcomeView;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

public class GenericClientFactoryImpl implements ClientFactory {
	/* History Management*/
	private final EventBus eventBus = new SimpleEventBus();
	private final PlaceController placeController = new PlaceController(eventBus);
	private final HistoryMapper historyMapper = GWT.create(HistoryMapper.class);
	private final PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
	
	/* Activity Managers*/
	private ActivityManager globalActivityManager;
	private ActivityManager appActivityManager;
	
	private SimplePanel appPanel;
	
	/* REST API Client */
	private final String apiURL = "http://localhost:8080/api";	
	private final KornellClient client = new KornellClient(apiURL);
	
	/* Views */
	private GenericHomeView genericHomeView;
	


	public GenericClientFactoryImpl() {
	}
	
	private void initActivityManagers() {
		initGlobalActivityManager();
		initAppActivityManager();		
	}
	
	private void initGlobalActivityManager() {
		globalActivityManager = new ActivityManager(new GlobalActivityMapper(this),eventBus);
		SimplePanel shell = new SimplePanel();
		shell.addStyleName("wrapper");
		RootPanel.get().add(shell);
	    globalActivityManager.setDisplay(shell);
	}

	private void initAppActivityManager() {
		appActivityManager =  new ActivityManager(new AppActivityMapper(this), eventBus);
		appPanel = new SimplePanel();
		appPanel.addStyleName("wrapper");
		appActivityManager.setDisplay(appPanel);
	}
	
	private void initHistoryHandler() {
		historyHandler.register(placeController, eventBus, new VitrinePlace());		
		historyHandler.handleCurrentHistory();
	}
	
	@Override
	public ClientFactory startApp() {
		initActivityManagers();
		initHistoryHandler();
		return this;
	}

	@Override
	public HomeView getHomeView() {
		if(genericHomeView == null){
			genericHomeView = new GenericHomeView(this, eventBus,placeController,historyHandler, client, appPanel);
		}
		return genericHomeView;
	}

	@Override
	public VitrineView getVitrineView() {
		return new GenericVitrineView(placeController,client);
	}

	@Override
	public WelcomeView getWelcomeView() {
		return new GenericWelcomeView(client);
	}

}
