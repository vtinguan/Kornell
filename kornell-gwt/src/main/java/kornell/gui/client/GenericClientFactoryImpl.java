package kornell.gui.client;

import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.api.client.UserSession;
import kornell.core.entity.EntityFactory;
import kornell.core.entity.Institution;
import kornell.core.event.EventFactory;
import kornell.core.lom.LOMFactory;
import kornell.core.to.CourseClassTO;
import kornell.core.to.CourseClassesTO;
import kornell.core.to.TOFactory;
import kornell.gui.client.personnel.Captain;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.personnel.Stalker;
import kornell.gui.client.presentation.GlobalActivityMapper;
import kornell.gui.client.presentation.HistoryMapper;
import kornell.gui.client.presentation.admin.home.AdminHomePlace;
import kornell.gui.client.presentation.course.CourseClassPlace;
import kornell.gui.client.presentation.vitrine.VitrinePlace;
import kornell.gui.client.util.ClientProperties;
import kornell.scorm.client.scorm12.CMIDataModel;
import kornell.scorm.client.scorm12.SCORM12Adapter;
import kornell.scorm.client.scorm12.SCORM12Binder;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

//TODO: Organize this big, messy class and interface
public class GenericClientFactoryImpl implements ClientFactory {
	public static final EntityFactory entityFactory = GWT.create(EntityFactory.class);
	public static final TOFactory toFactory = GWT.create(TOFactory.class);
	public static final LOMFactory lomFactory = GWT.create(LOMFactory.class);
	public static final EventFactory eventFactory = GWT.create(EventFactory.class);

	/* History Management */
	private final EventBus bus = new SimpleEventBus();
	private final PlaceController placeCtrl = new PlaceController(bus);
	private final HistoryMapper historyMapper = GWT.create(HistoryMapper.class);
	private final PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);

	/* Activity Managers */
	private ActivityManager globalActivityManager;

	/* GUI */
	private ViewFactory viewFactory;
	private Place defaultPlace;

	private UserSession session;

	public GenericClientFactoryImpl() {
	}

	private void initActivityManagers() {
		initGlobalActivityManager();
	}

	private void initGlobalActivityManager() {
		globalActivityManager = new ActivityManager(new GlobalActivityMapper(this), bus);
		globalActivityManager.setDisplay(viewFactory.getShell());
	}

	private void initHistoryHandler(Place defaultPlace) {
		historyHandler.register(placeCtrl, bus, defaultPlace);
		new Stalker(bus, session, historyMapper);
		historyHandler.handleCurrentHistory();
		if (!session.isAuthenticated())
			placeCtrl.goTo(defaultPlace);
	}

	@Override
	public void startApp() {
		final Callback<CourseClassesTO> courseClassesCallback = new Callback<CourseClassesTO>() {
			@Override
			public void ok(final CourseClassesTO courseClasses) {
				CourseClassTO courseClassTO = null;
				for (CourseClassTO courseClassTmp : courseClasses.getCourseClasses()) {
					if (courseClassTmp.getCourseClass().getInstitutionUUID().equals(Dean.getInstance().getInstitution().getUUID())) {
						courseClassTO = courseClassTmp;
					}
				}
				Dean.getInstance().setCourseClassTO(courseClassTO);
				setDefaultPlace(new CourseClassPlace(courseClassTO.getCourseClass().getUUID()));
				startAuthenticated(session);
			}
		};
		
		final Callback<Institution> institutionCallback = new Callback<Institution>() {
			@Override
			public void ok(final Institution institution) {
				Dean.init(session, bus, institution);
				if (session.isAuthenticated()) {
					session.getCourseClassesTO(courseClassesCallback);
				} else {
					startAnonymous(session);
				}
			}
			
			@Override
			public void unauthorized(){
				startAnonymous(session);
			}
		};
		
		
		
		UserSession.current(new Callback<UserSession>() {
			@Override
			public void ok(UserSession userSession) {
				session = userSession;
				session.getInstitutionByName(getInstitutionNameFromLocation(), institutionCallback);
			}

			@Override
			public void unauthorized() {
				ClientProperties.remove("Authorization");
			}

			private String getInstitutionNameFromLocation() {
				String institutionName = Window.Location.getParameter("institution");
				if (institutionName == null) {
					institutionName = Window.Location.getHostName().split("\\.")[0];
				}
				return institutionName;
			}

		});
	}

	private void startAnonymous(UserSession session) {
		ClientProperties.remove("Authorization");
		defaultPlace = new VitrinePlace();
		startClient();
	}

	private void startAuthenticated(UserSession session) {
		if (session.isDean()) {
			defaultPlace = new AdminHomePlace();
			startClient();
		} else {
			startClient();
		}
	}

	protected void startClient() {
		initGUI();
		initActivityManagers();
		initHistoryHandler(defaultPlace);
		initException();
		initSCORM();
		initPersonnel();
	}

	private void initGUI() {
		viewFactory = new GenericViewFactoryImpl(this);
		viewFactory.initGUI();
	}

	private void initPersonnel() {
		new Captain(bus, placeCtrl, Dean.getInstance().getInstitution().getUUID());
	}

	private void initSCORM() {		
		CMIDataModel cmi = new CMIDataModel(getKornellClient());
		SCORM12Binder.bind(new SCORM12Adapter(cmi));
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
	public KornellClient getKornellClient() {
		return session;
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
	public UserSession getUserSession() {
		return session;
	}

	@Override
	public ViewFactory getViewFactory() {
		return viewFactory;
	}
}
