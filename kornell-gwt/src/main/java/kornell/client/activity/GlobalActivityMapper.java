package kornell.client.activity;


import kornell.client.ClientFactory;
import kornell.client.presenter.home.HomePlace;
import kornell.client.presenter.home.HomePresenter;
import kornell.client.presenter.vitrine.VitrinePlace;
import kornell.client.presenter.welcome.WelcomePlace;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

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