package kornell.client.view.generic;

import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.api.client.data.Person;
import kornell.client.ClientFactory;
import kornell.client.activity.AppActivityMapper;
import kornell.client.presenter.home.HomePlace;
import kornell.client.presenter.home.HomeView;
import kornell.client.presenter.welcome.WelcomePlace;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class GenericHomeView  extends Composite implements HomeView {
	interface MyUiBinder extends UiBinder<Widget, GenericHomeView> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private KornellClient client;
	
	//private PlaceController placeCtrl;
	@UiField Label lblWelcome;
	
	@UiField SimplePanel appPanel;
	private EventBus eventBus;
	private PlaceController placeCtrl;
	private PlaceHistoryHandler historyHandler;
	private ClientFactory factory;
	
	public GenericHomeView(
			ClientFactory factory,
			EventBus eventBus,
			PlaceController placeCtrl,
			PlaceHistoryHandler historyHandler,
			KornellClient client) {
		GWT.log("GenericHomeView()");
		this.factory = factory;
		this.historyHandler = historyHandler;
		this.placeCtrl = placeCtrl;
		this.client = client;
		this.eventBus = eventBus;
		
		client.getCurrentUser(new Callback(){
			@Override
			protected void ok(Person person) {
				lblWelcome.setText("Olá "+person.getFullName());
			}
		});
	    initWidget(uiBinder.createAndBindUi(this));
	    initAppActivityManager();
	    
	}
	
	
	private ActivityManager appActivityManager;
	private void initAppActivityManager() {
		appActivityManager = new ActivityManager(new AppActivityMapper(factory), eventBus);
		appActivityManager.setDisplay(appPanel);		
	}


	@Override
	public void setPresenter(Presenter presenter) {
	 		
	}
	
	@UiHandler("welcome")
	public void onWelcome(ClickEvent e){
		placeCtrl.goTo(new WelcomePlace());
	}
	

}
