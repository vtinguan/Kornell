package kornell.gui.client.personnel;

import kornell.api.client.UserSession;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.event.LoginEvent;
import kornell.gui.client.event.LoginEventHandler;
import kornell.gui.client.event.LogoutEvent;
import kornell.gui.client.event.LogoutEventHandler;
import kornell.gui.client.presentation.vitrine.VitrinePlace;
import kornell.gui.client.util.ClientProperties;

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
	private String institutionUUID;

	public Captain(EventBus bus, PlaceController placeCtrl,String institutionUUID) { 
		this.placeCtrl = placeCtrl;		
		this.institutionUUID = institutionUUID;
		bus.addHandler(LogoutEvent.TYPE, this);
		bus.addHandler(LoginEvent.TYPE, this);
	}

	@Override
	public void onLogout() {
		placeCtrl.goTo(VitrinePlace.instance);
		//TODO find a better way to clear the cache
		//the last user's infos were appearing when I 
		//logged in with another one
		//Window.Location.reload();
		//TODO remove this also
		ClientProperties.remove("Authorization");
	}

	@Override
	public void onLogin(UserInfoTO user) {
		UserSession.setCurrentPerson(user.getPerson().getUUID(),institutionUUID);
	}


}
