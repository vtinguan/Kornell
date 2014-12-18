package kornell.gui.client;

import java.util.logging.Logger;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.EntityFactory;
import kornell.core.entity.RoleCategory;
import kornell.core.entity.RoleType;
import kornell.core.event.EventFactory;
import kornell.core.lom.LOMFactory;
import kornell.core.to.TOFactory;
import kornell.core.to.UserHelloTO;
import kornell.gui.client.mvp.AsyncActivityManager;
import kornell.gui.client.mvp.AsyncActivityMapper;
import kornell.gui.client.mvp.GlobalActivityMapper;
import kornell.gui.client.mvp.HistoryMapper;
import kornell.gui.client.personnel.Captain;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.personnel.MrPostman;
import kornell.gui.client.personnel.Stalker;
import kornell.gui.client.presentation.admin.courseclass.courseclass.AdminCourseClassPlace;
import kornell.gui.client.presentation.admin.courseclass.courseclasses.AdminCourseClassesPlace;
import kornell.gui.client.presentation.message.compose.MessageComposePresenter;
import kornell.gui.client.presentation.util.KornellNotification;
import kornell.gui.client.presentation.vitrine.VitrinePlace;
import kornell.gui.client.presentation.welcome.WelcomePlace;
import kornell.gui.client.util.ClientProperties;
import kornell.scorm.client.scorm12.SCORM12Adapter;
import kornell.scorm.client.scorm12.SCORM12Binder;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryHandler.DefaultHistorian;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

//TODO: Organize this big, messy class and interface
public class GenericClientFactoryImpl implements ClientFactory {
	Logger logger = Logger.getLogger(GenericClientFactoryImpl.class.getName());

	public static final EntityFactory entityFactory = GWT
			.create(EntityFactory.class);
	public static final TOFactory toFactory = GWT.create(TOFactory.class);
	public static final LOMFactory lomFactory = GWT.create(LOMFactory.class);
	public static final EventFactory eventFactory = GWT
			.create(EventFactory.class);

	/* History Management */
	private final EventBus bus = new SimpleEventBus();
	private final PlaceController placeCtrl = new PlaceController(bus);
	private final HistoryMapper historyMapper = GWT.create(HistoryMapper.class);
	private final DefaultHistorian historian = GWT
			.create(DefaultHistorian.class);
	private final PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(
			historyMapper);

	/* Activity Managers */
	private ActivityManager globalActivityManager;

	/* GUI */
	private ViewFactory viewFactory;
	private Place defaultPlace;
	private KornellSession session = new KornellSession(bus);

	public GenericClientFactoryImpl() {
	}

	private void initActivityManagers() {
		initGlobalActivityManager();
	}

	private void initGlobalActivityManager() {
		AsyncActivityMapper activityMapper = new GlobalActivityMapper(this);
		AsyncActivityManager activityManager = new AsyncActivityManager(activityMapper, bus);
		activityManager.setDisplay(viewFactory.getShell());
	}

	private void initHistoryHandler(Place defaultPlace) {

		//AppPlaceHistoryMapper historyMapper= GWT.create(AppPlaceHistoryMapper.class);
		//PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
		//historyHandler.register(placeController, eventBus, defaultPlace);
		
		historyHandler.register(placeCtrl, bus, defaultPlace);
		// sessions that arent authenticated, go to the default place
		// except if it's a vitrineplace, then let the history take care of it
		if (!session.isAuthenticated()
				&& historian.getToken().indexOf("vitrine") == -1) {
			placeCtrl.goTo(defaultPlace);
		}
		historyHandler.handleCurrentHistory();
	}

	@Override
	public void startApp() {
		final Callback<UserHelloTO> userHelloCallback = new Callback<UserHelloTO>() {
			@Override
			public void ok(final UserHelloTO userHelloTO) {
				session.setCurrentUser(userHelloTO.getUserInfoTO());
				if(userHelloTO.getInstitution() == null) {
					KornellNotification.show("Instituição não encontrada.", AlertType.ERROR, -1);
				} else {					
					Dean.init(session, bus, userHelloTO.getInstitution());
					if (session.isAuthenticated()) {
						boolean isAdmin = (RoleCategory.hasRole(session.getCurrentUser().getRoles(), RoleType.courseClassAdmin) 
								|| session.isInstitutionAdmin());
						setDefaultPlace(isAdmin ? new AdminCourseClassesPlace() : new WelcomePlace());
						startAuthenticated(session);
					} else {
						startAnonymous();
					}
				}
			}
		};

		session.user().getUserHello(Window.Location.getParameter("institution"), Window.Location.getHostName(), userHelloCallback);
		
	}

	private void startAnonymous() {
		ClientProperties.remove(ClientProperties.X_KNL_A);
		defaultPlace = new VitrinePlace();
		startClient();
	}

	private void startAuthenticated(KornellSession session) {
		if (session.isCourseClassAdmin()) {
			defaultPlace = new AdminCourseClassesPlace();
		}
		startClient();
	}

	protected void startClient() {
		initGUI();
		initActivityManagers();
		initHistoryHandler(defaultPlace);
		initException();
		initSCORM12();
		initPersonnel();
	}

	private void initGUI() {
		viewFactory = new GenericViewFactoryImpl(this);
		viewFactory.initGUI();
	}

	private void initPersonnel() {
		new Captain(bus, session, placeCtrl);
		new Stalker(bus, session);
		new MrPostman(new MessageComposePresenter(placeCtrl, session, viewFactory, entityFactory),  bus, session.chatThreads(), placeCtrl);
		
	}

	private void initSCORM12() {
		SCORM12Binder.bind(new SCORM12Adapter(bus, session));
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
	public PlaceController getPlaceController() {
		return placeCtrl;
	}

	@Override
	public HistoryMapper getHistoryMapper() {
		return historyMapper;
	}

	@Override
	public EventBus getEventBus() {
		return bus;
	}

	@Override
	public Place getDefaultPlace() {
		return defaultPlace;
	}

	@Override
	public void setDefaultPlace(Place place) {
		this.defaultPlace = place;
	}

	@Override
	public EntityFactory getEntityFactory() {
		return entityFactory;
	}

	@Override
	public TOFactory getTOFactory() {
		return toFactory;
	}

	@Override
	public LOMFactory getLOMFactory() {
		return lomFactory;
	}

	@Override
	public EventFactory getEventFactory() {
		return eventFactory;
	}

	@Override
	public ViewFactory getViewFactory() {
		return viewFactory;
	}

	@Override
	public void logState() {
		// TODO
	}

	@Override
	public KornellSession getKornellSession() {
		return session;
	}

	@Override
	public void setKornellSession(KornellSession session) {
		this.session = session;
	}
}
