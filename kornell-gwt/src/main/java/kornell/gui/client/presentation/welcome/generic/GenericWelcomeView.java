package kornell.gui.client.presentation.welcome.generic;

import kornell.api.client.KornellClient;
import kornell.api.client.KornellSession;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.event.MenuLeftWelcomeEvent;
import kornell.gui.client.event.MenuLeftWelcomeEventHandler;
import kornell.gui.client.presentation.welcome.WelcomePlace;
import kornell.gui.client.presentation.welcome.WelcomeView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class GenericWelcomeView extends Composite implements WelcomeView {
	interface MyUiBinder extends UiBinder<Widget, GenericWelcomeView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	@UiField
	FlowPanel pnlWelcome;
	@UiField
	FlowPanel pnlMenuLeft;
	
	private KornellSession session;
	private PlaceController placeCtrl;
	private final EventBus bus;
	private static KornellConstants constants = GWT.create(KornellConstants.class);
	private WelcomeView.Presenter presenter;
	
	private GenericMenuLeftView menuLeftView;	
	private GenericWelcomeCoursesView coursesView;
	private GenericWelcomeNotificationsView notificationsView;
	private GenericWelcomeMyParticipationView myParticipationView;
	private GenericWelcomeProfileView profileView;
	
	private static String COURSES_VIEW = constants.courses();
	private static String NOTIFICATIONS_VIEW = constants.notifications();
	private static String MY_PARTICIPATION_VIEW = constants.myParticipation();
	private static String PROFILE_VIEW = constants.profile();
		
	
	public GenericWelcomeView(EventBus bus, KornellSession session, PlaceController placeCtrl) {
		this.session = session;
		this.placeCtrl = placeCtrl;
		this.bus = bus;
		initWidget(uiBinder.createAndBindUi(this));
		
		bus.addHandler(MenuLeftWelcomeEvent.TYPE, new MenuLeftWelcomeEventHandler() {
			@Override
			public void onItemSelected(MenuLeftWelcomeEvent event) {
				display(event.getMenuLeftItemSelected());
			}
		});
		
		bus.addHandler(PlaceChangeEvent.TYPE, new PlaceChangeEvent.Handler() {
			@Override
			public void onPlaceChange(PlaceChangeEvent event) {
				if(event.getNewPlace() instanceof WelcomePlace)
					initData();		
			}
		});
		
		initData();		
		
	}
	
	private void initData() {
		//pnlMenuLeft.add(getMenuLeft());
		pnlMenuLeft.setVisible(false);
		display(COURSES_VIEW);
	}

	private void display(String viewName) {
		pnlWelcome.clear();
		if(COURSES_VIEW.equals(viewName)){
			pnlWelcome.add(getCoursesView());
			
		} else if(NOTIFICATIONS_VIEW.equals(viewName)){
			pnlWelcome.add(getNotificationsView());
			
		} else if(MY_PARTICIPATION_VIEW.equals(viewName)){
			pnlWelcome.add(getMyParticipationView());
			
		} else {
			pnlWelcome.add(getProfileView());
		}
	}

	private Widget getMenuLeft() {
		if(menuLeftView == null)
			menuLeftView = new GenericMenuLeftView(bus, session, placeCtrl);
		return menuLeftView;
	}

	private Widget getCoursesView() {
		coursesView = new GenericWelcomeCoursesView(bus, session, placeCtrl);
		coursesView.setPresenter(presenter);
		return coursesView;
	}

	private Widget getNotificationsView() {
		if(notificationsView == null)
			notificationsView = new GenericWelcomeNotificationsView(session, placeCtrl);
		return notificationsView;
	}

	private Widget getMyParticipationView() {
		if(myParticipationView == null)
			myParticipationView = new GenericWelcomeMyParticipationView(session, placeCtrl);
		return myParticipationView;
	}

	private Widget getProfileView() {
		if(profileView == null)
			profileView = new GenericWelcomeProfileView(session, placeCtrl);
		return profileView;
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

}