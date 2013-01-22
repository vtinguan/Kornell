package kornell.gui.client.presentation;


import kornell.gui.client.ClientFactory;
import kornell.gui.client.presentation.activity.AtividadeActivity;
import kornell.gui.client.presentation.activity.AtividadePlace;
import kornell.gui.client.presentation.activity.AtividadePresenter;
import kornell.gui.client.presentation.home.HomeActivity;
import kornell.gui.client.presentation.home.HomePlace;
import kornell.gui.client.presentation.vitrine.VitrineActivity;
import kornell.gui.client.presentation.vitrine.VitrinePlace;
import kornell.gui.client.presentation.welcome.WelcomeActivity;
import kornell.gui.client.presentation.welcome.WelcomePlace;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.place.shared.Place;

/**
 * A mapping of places to activities used by this application.
 */
public class GlobalActivityMapper implements ActivityMapper {
	private ClientFactory factory;

	public GlobalActivityMapper(ClientFactory clientFactory) {
    this.factory = clientFactory;
  }

  /** TODO: This may suck fast */
  public Activity getActivity(final Place place) {
	  GWT.log("Global Manager looling for "+place.toString());
    if (place instanceof HomePlace) {
      return new HomeActivity(factory);
    }
    if (place instanceof VitrinePlace){
    	return new VitrineActivity(factory);
    }
	if (place instanceof WelcomePlace) {
		return new WelcomeActivity(factory);
	}
	if (place instanceof AtividadePlace) {
		AtividadePresenter atividadePresenter = factory.getActivityPresenter();
		atividadePresenter.setPlace((AtividadePlace)place);
		return new AtividadeActivity(atividadePresenter);
	}

    return null;
  }
}