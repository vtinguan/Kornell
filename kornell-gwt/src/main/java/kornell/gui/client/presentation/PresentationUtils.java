package kornell.gui.client.presentation;

import kornell.gui.client.presentation.vitrine.VitrinePlace;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.web.bindery.event.shared.EventBus;

public class PresentationUtils {

	public static void invisibleOnVitrine(EventBus eventBus,final UIObject ui) {
		eventBus.addHandler(PlaceChangeEvent.TYPE, new PlaceChangeEvent.Handler() {			
			@Override
			public void onPlaceChange(PlaceChangeEvent event) {
				Place newPlace = event.getNewPlace();
				boolean isAtVitrine = newPlace instanceof VitrinePlace;
				ui.setVisible(! isAtVitrine);							
			}
		});
	}

}
