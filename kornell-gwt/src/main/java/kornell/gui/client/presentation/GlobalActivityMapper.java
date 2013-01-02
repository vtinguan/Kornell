package kornell.gui.client.presentation;


import kornell.gui.client.ClientFactory;
import kornell.gui.client.presentation.home.HomeActivity;
import kornell.gui.client.presentation.home.HomePlace;
import kornell.gui.client.presentation.vitrine.VitrineActivity;
import kornell.gui.client.presentation.vitrine.VitrinePlace;
import kornell.gui.client.presentation.welcome.WelcomePlace;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.place.shared.Place;

/**
 * A mapping of places to activities used by this application.
 */
public class GlobalActivityMapper implements ActivityMapper {
	private ClientFactory clientFactory;

	public GlobalActivityMapper(ClientFactory clientFactory) {
    this.clientFactory = clientFactory;
  }

  /** TODO: This may suck fast */
  public Activity getActivity(final Place place) {
	  GWT.log("Global Manager looling for "+place.toString());
    if (place instanceof HomePlace
    		|| place instanceof WelcomePlace) {
      return new HomeActivity(clientFactory);
    }
    if (place instanceof VitrinePlace){
    	return new VitrineActivity(clientFactory);
    }

    return null;
  }
}