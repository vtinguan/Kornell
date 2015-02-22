package kornell.gui.client.personnel;

import java.util.logging.Logger;

import kornell.api.client.KornellSession;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.event.LoginEvent;
import kornell.gui.client.event.LoginEventHandler;
import kornell.gui.client.event.LogoutEvent;
import kornell.gui.client.event.LogoutEventHandler;
import kornell.gui.client.presentation.vitrine.VitrinePlace;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Manages navigation
 * 
 * @author faermanj
 */
public class Captain implements LogoutEventHandler, LoginEventHandler{
	Logger logger = Logger.getLogger(Captain.class.getName());
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
		placeCtrl.goTo(VitrinePlace.instance);
		Window.Location.reload();
	}

	@Override
	public void onLogin(UserInfoTO user) {
		logger.info("User logged in as "+user.getUsername());
	}


}
