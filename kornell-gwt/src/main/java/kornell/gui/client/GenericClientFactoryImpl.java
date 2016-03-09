package kornell.gui.client;

import java.util.Date;
import java.util.logging.Logger;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.Enrollment;
import kornell.core.entity.EnrollmentState;
import kornell.core.entity.EntityFactory;
import kornell.core.entity.InstitutionType;
import kornell.core.entity.RoleCategory;
import kornell.core.entity.RoleType;
import kornell.core.error.KornellErrorTO;
import kornell.core.event.EventFactory;
import kornell.core.lom.LOMFactory;
import kornell.core.to.CourseClassesTO;
import kornell.core.to.TOFactory;
import kornell.core.to.UserHelloTO;
import kornell.gui.client.event.CourseClassesFetchedEvent;
import kornell.gui.client.mvp.AsyncActivityManager;
import kornell.gui.client.mvp.AsyncActivityMapper;
import kornell.gui.client.mvp.GlobalActivityMapper;
import kornell.gui.client.mvp.HistoryMapper;
import kornell.gui.client.personnel.Captain;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.personnel.MrPostman;
import kornell.gui.client.personnel.Stalker;
import kornell.gui.client.presentation.admin.courseclass.courseclasses.AdminCourseClassesPlace;
import kornell.gui.client.presentation.classroom.ClassroomPlace;
import kornell.gui.client.presentation.message.compose.MessageComposePresenter;
import kornell.gui.client.presentation.vitrine.VitrinePlace;
import kornell.gui.client.presentation.welcome.WelcomePlace;
import kornell.gui.client.util.ClientProperties;
import kornell.gui.client.util.view.KornellMaintenance;
import kornell.gui.client.util.view.KornellNotification;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
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

	public static final EventBus EVENT_BUS = GWT.create(SimpleEventBus.class);
	
	/* History Management */
//	private final EventBus bus = new SimpleEventBus();
	private final PlaceController placeCtrl = new PlaceController(EVENT_BUS);
	private final HistoryMapper historyMapper = GWT.create(HistoryMapper.class);
	private final DefaultHistorian historian = GWT
			.create(DefaultHistorian.class);
	private final PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(
			historyMapper);

	/* GUI */
	private ViewFactory viewFactory;
	private Place defaultPlace;
	private Place homePlace;
	private KornellSession session = new KornellSession(EVENT_BUS);

	public GenericClientFactoryImpl() {
	}

	private void initActivityManagers() {
		initGlobalActivityManager();
	}

	private void initGlobalActivityManager() {
		AsyncActivityMapper activityMapper = new GlobalActivityMapper(this);
		AsyncActivityManager activityManager = new AsyncActivityManager(activityMapper, EVENT_BUS);
		activityManager.setDisplay(viewFactory.getShell());
	}

	private void initHistoryHandler(Place defaultPlace) {

		//AppPlaceHistoryMapper historyMapper= GWT.create(AppPlaceHistoryMapper.class);
		//PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
		//historyHandler.register(placeController, eventBus, defaultPlace);
		
		historyHandler.register(placeCtrl, EVENT_BUS, defaultPlace);
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
		//remove token cookie on page load
		final Callback<UserHelloTO> userHelloCallback = new Callback<UserHelloTO>() {
			@Override
			public void ok(final UserHelloTO userHelloTO) {
				doCallbackOk(userHelloTO);
			}
			
			@Override
			public void unauthorized(KornellErrorTO kornellErrorTO){
				//this case means someone entered a URL in the bar with an expired token in local storage
				//so we clear his old token and we do the call to hello again
				ClientProperties.remove(ClientProperties.X_KNL_TOKEN);
				ClientProperties.removeCookie(ClientProperties.X_KNL_TOKEN);
				final Callback<UserHelloTO> userManualAccessCallback = new Callback<UserHelloTO>() {
					@Override
					public void ok(final UserHelloTO userHelloTO) {
						doCallbackOk(userHelloTO);
					}
				};
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						session.user().getUserHello(Window.Location.getParameter("institution"), Window.Location.getHostName(), userManualAccessCallback);
					}
				});
			}
			
			@Override
			public void internalServerError(KornellErrorTO kornellErrorTO) {
				KornellMaintenance.show();
			}
			
			@Override
			public void serviceUnavailable() {
				KornellMaintenance.show();
			}
			
			private void doCallbackOk(final UserHelloTO userHelloTO) {
				session.setCurrentUser(userHelloTO.getUserInfoTO());
				if(userHelloTO.getInstitution() == null) {
					KornellNotification.show("Instituição não encontrada.", AlertType.ERROR, -1);
				} else {		
					Dean.init(session, EVENT_BUS, userHelloTO.getInstitution());
					final Callback<CourseClassesTO> courseClassesCallback = new Callback<CourseClassesTO>() {
						@Override
						public void ok(CourseClassesTO courseClassesTO) {
							EVENT_BUS.fireEvent(new CourseClassesFetchedEvent(courseClassesTO));	
							setHomePlace(new WelcomePlace());
							startAuthenticated(courseClassesTO);
						}
					};
					if (session.isAuthenticated()) {
						session.courseClasses().getCourseClassesTO(courseClassesCallback);
					} else {
						startAnonymous();
					}
				}
			}
		};
		session.user().getUserHello(Window.Location.getParameter("institution"), Window.Location.getHostName(), userHelloCallback);
	}

	private void startAnonymous() {
		ClientProperties.remove(ClientProperties.X_KNL_TOKEN);
		setDefaultPlace(new VitrinePlace());
		startClient(null);
	}

	private void startAuthenticated(CourseClassesTO courseClassesTO) {
		if(RoleCategory.hasRole(session.getCurrentUser().getRoles(), RoleType.courseClassAdmin) 
				|| session.isInstitutionAdmin()){
			setDefaultPlace(new AdminCourseClassesPlace());
		} else if(InstitutionType.DASHBOARD.equals(Dean.getInstance().getInstitution().getInstitutionType())){
			setDefaultPlace(getHomePlace());
		} else {
			setDefaultPlace(new WelcomePlace());
		}
		startClient(courseClassesTO);
	}

	protected void startClient(CourseClassesTO courseClassesTO) {
		initGUI(courseClassesTO);
		initActivityManagers();
		initHistoryHandler(defaultPlace);
		initException();
		initPersonnel();
	}

	private void initGUI(CourseClassesTO courseClassesTO) {
		viewFactory = new GenericViewFactoryImpl(this, courseClassesTO);
		viewFactory.initGUI();
	}

	private void initPersonnel() {
		new Captain(EVENT_BUS, session, placeCtrl);
		new Stalker(EVENT_BUS, session);

		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				new MrPostman(new MessageComposePresenter(placeCtrl, session, viewFactory, entityFactory),  EVENT_BUS, session.chatThreads(), placeCtrl);
				viewFactory.getMessagePresenter();
				viewFactory.getMessagePresenterClassroomGlobalChat();
				viewFactory.getMessagePresenterClassroomTutorChat();
                if (session.getCurrentUser() != null && session.hasAnyAdminRole(session.getCurrentUser().getRoles())) {
					viewFactory.getMessagePresenterCourseClass();
				}
			}
		});
	}

	private void initException() {
		GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
			@Override
			public void onUncaughtException(Throwable e) {
                if (Window.Location.getHostName().indexOf("localhost") >= 0
                        || Window.Location.getHostName().indexOf("127.0.0.1") >= 0) {
                    KornellNotification.show(e.getMessage(), AlertType.ERROR, 0);
                }
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
		return EVENT_BUS;
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
	public Place getHomePlace() {
		return homePlace != null ? homePlace : defaultPlace;
	}

	@Override
	public void setHomePlace(Place place) {
		String enrollmentUUID = null;
		if(session.getCurrentUser() != null &&
				session.getCurrentUser().getEnrollments() != null &&
				InstitutionType.DASHBOARD.equals(Dean.getInstance().getInstitution().getInstitutionType())){
			Date date = new Date(0);
			for (Enrollment enrollment : session.getCurrentUser().getEnrollments().getEnrollments()) {
				//get latest active enrollment on a class (if no enrollment was found yet, get non active enrollment)
				if(enrollment.getEnrolledOn().after(date) && enrollment.getCourseClassUUID() != null){
					if(EnrollmentState.enrolled.equals(enrollment.getState()) || enrollmentUUID == null){
						date = enrollment.getEnrolledOn();
						enrollmentUUID = enrollment.getUUID();
					}
				}				
			}			
		}
		if(enrollmentUUID != null){
			place = new ClassroomPlace(enrollmentUUID);
		}
		this.homePlace = place;
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
