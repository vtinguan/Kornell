package kornell.client.activity;

import kornell.client.ClientFactory;
import kornell.client.presenter.welcome.WelcomePlace;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.place.shared.Place;

public class AppActivityMapper implements ActivityMapper {
	  private final ClientFactory clientFactory;

	  public AppActivityMapper(ClientFactory clientFactory) {
		GWT.log("AppManager created");
	    this.clientFactory = clientFactory;
	  }

	@Override
	public Activity getActivity(Place place) {
		GWT.log("App Manager looling for "+place.toString());
		if (place instanceof WelcomePlace) {
			return new WelcomeActivity(clientFactory);
		}
		return null;
	}

}
