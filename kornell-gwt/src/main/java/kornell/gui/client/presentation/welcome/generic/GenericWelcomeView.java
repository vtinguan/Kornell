package kornell.gui.client.presentation.welcome.generic;

import kornell.api.client.KornellSession;
import kornell.core.to.TOFactory;
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
	private TOFactory toFactory;
	
	private GenericWelcomeCoursesView coursesView;
	private static String COURSES_VIEW = constants.courses();
	private static String NOTIFICATIONS_VIEW = constants.notifications();
	private static String MY_PARTICIPATION_VIEW = constants.myParticipation();
	private static String PROFILE_VIEW = constants.profile();
		
	
	public GenericWelcomeView(EventBus bus, KornellSession session, PlaceController placeCtrl, TOFactory toFactory) {
		this.session = session;
		this.placeCtrl = placeCtrl;
		this.toFactory = toFactory;
		this.bus = bus;
		initWidget(uiBinder.createAndBindUi(this));
		
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
		pnlMenuLeft.setVisible(false);
		pnlWelcome.clear();
		pnlWelcome.add(getCoursesView());
	}

	private Widget getCoursesView() {
		coursesView = new GenericWelcomeCoursesView(bus, session, placeCtrl, toFactory);
		coursesView.setPresenter(presenter);
		return coursesView;
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

}