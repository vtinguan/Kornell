package kornell.gui.client.personnel;

import kornell.api.client.KornellSession;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.event.LoginEvent;
import kornell.gui.client.event.LoginEventHandler;
import kornell.gui.client.event.LogoutEvent;
import kornell.gui.client.event.LogoutEventHandler;
import kornell.gui.client.presentation.vitrine.VitrinePlace;
import kornell.gui.client.util.ClientProperties;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Manages navigation
 * 
 * @author faermanj
 */
public class Captain implements LogoutEventHandler, LoginEventHandler{
	private PlaceController placeCtrl;
	private KornellSession session;

	public Captain(EventBus bus, KornellSession session, PlaceController placeCtrl) { 
		this.placeCtrl = placeCtrl;		
		this.session = session;
		bus.addHandler(LogoutEvent.TYPE, this);
		bus.addHandler(LoginEvent.TYPE, this);
	}

	@Override
	public void onLogout() {		
		session.logout();
		Window.Location.reload();
		placeCtrl.goTo(VitrinePlace.instance);
	}

	@Override
	public void onLogin(UserInfoTO user) {
		GWT.log("User logged in as "+user.getUsername());
	}


}
