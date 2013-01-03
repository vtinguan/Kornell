package kornell.gui.client.presentation.home.generic;

import kornell.api.client.KornellClient;
import kornell.gui.client.ClientFactory;
import kornell.gui.client.presentation.AppActivityMapper;
import kornell.gui.client.presentation.home.HomeView;
import kornell.gui.client.presentation.welcome.WelcomePlace;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class GenericHomeView  extends Composite implements HomeView {
	interface MyUiBinder extends UiBinder<Widget, GenericHomeView> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	
	//private PlaceController placeCtrl;
	
	@UiField(provided=true) SimplePanel appPanel;
	private EventBus eventBus;

	private ClientFactory factory;


	private PlaceController placeCtrl;
	
	public GenericHomeView(
			ClientFactory factory,
			EventBus eventBus,
			PlaceController placeCtrl,
			PlaceHistoryHandler historyHandler,
			KornellClient client, SimplePanel appPanel) {
		GWT.log("GenericHomeView()");
		this.factory = factory;
		this.eventBus = eventBus;
		this.appPanel = appPanel; 
		this.placeCtrl = placeCtrl;
	    initWidget(uiBinder.createAndBindUi(this));	    
	}
	
	
	private ActivityManager appActivityManager;
	


	@Override
	public void setPresenter(Presenter presenter) {
	 		
	}
	
	@UiHandler("lnkWelcome")
	public void onLnkWelcome(ClickEvent e){
		placeCtrl.goTo(new WelcomePlace());
	}
	
	
}
