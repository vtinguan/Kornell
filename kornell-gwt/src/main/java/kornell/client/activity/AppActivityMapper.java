package kornell.client.activity;


import kornell.client.ClientFactory;
import kornell.client.presenter.home.HomePlace;
import kornell.client.presenter.home.HomePresenter;
import kornell.client.presenter.vitrine.VitrinePlace;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

/**
 * A mapping of places to activities used by this application.
 */
public class AppActivityMapper implements ActivityMapper {

  private final ClientFactory clientFactory;

  public AppActivityMapper(ClientFactory clientFactory) {
    this.clientFactory = clientFactory;
  }

  /** TODO: This may suck fast */
  public Activity getActivity(final Place place) {
    if (place instanceof HomePlace) {
      // The list of tasks.
      return new AbstractActivity() {
        @Override
        public void start(AcceptsOneWidget panel, EventBus eventBus) {
        HomePresenter presenter = new HomePresenter(clientFactory);
        panel.setWidget(presenter);
        }

        /*
         * Note no call to presenter.stop(). The TaskListViews do that
         * themselves as a side effect of setPresenter.
         */
      };
    }
    if (place instanceof VitrinePlace){
    	return new VitrineActivity(clientFactory);
    }
    /*
    if (place instanceof TaskPlace) {
      // Editable view of a task.
      return new TaskActivity(clientFactory, (TaskPlace) place);
    }
    */

    return null;
  }
}