package kornell.gui.client.personnel;

import kornell.gui.client.event.LogoutEvent;
import kornell.gui.client.event.LogoutEventHandler;
import kornell.gui.client.presentation.vitrine.VitrinePlace;

import com.google.gwt.place.shared.PlaceController;
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
	}

}
