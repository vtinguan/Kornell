package kornell.gui.client.personnel;

import kornell.core.to.UserInfoTO;
import kornell.gui.client.event.LoginEvent;
import kornell.gui.client.event.LoginEventHandler;
import kornell.gui.client.event.LogoutEvent;
import kornell.gui.client.event.LogoutEventHandler;
import kornell.gui.client.presentation.vitrine.VitrinePlace;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Manages navigation
 * 
 * @author faermanj
 */
public class Captain implements LogoutEventHandler, LoginEventHandler{
	private PlaceController placeCtrl;
	private String institutionUUID;

	public Captain(EventBus bus, PlaceController placeCtrl,String institutionUUID) { 
		this.placeCtrl = placeCtrl;		
		this.institutionUUID = institutionUUID;
		bus.addHandler(LogoutEvent.TYPE, this);
		bus.addHandler(LoginEvent.TYPE, this);
	}

	@Override
	public void onLogout() {
		//TODO find a better way to clear the cache
		//the last user's infos were appearing when I 
		//logged in with another one
		//Window.Location.reload();
		//TODO remove this also
		placeCtrl.goTo(VitrinePlace.instance);
	}

	@Override
	public void onLogin(UserInfoTO user) {
		GWT.log("User logged in as "+user.getUsername());
	}


}
