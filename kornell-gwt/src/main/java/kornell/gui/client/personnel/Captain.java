package kornell.gui.client.personnel;

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
public class Captain implements LogoutEventHandler{
	private PlaceController placeCtrl;

	public Captain(EventBus bus, PlaceController placeCtrl) { 
		this.placeCtrl = placeCtrl;		
		bus.addHandler(LogoutEvent.TYPE, this);
	}

	@Override
	public void onLogout() {
		placeCtrl.goTo(VitrinePlace.instance);
		//TODO find a better way to clear the cache
		//the last user's infos were appearing when I 
		//logged in with another one
		Window.Location.reload();
		//TODO remove this also
		ClientProperties.remove("Authorization");
	}

}
