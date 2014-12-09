package kornell.gui.client.presentation.admin.courseclass.courseclass;

import kornell.api.client.KornellSession;
import kornell.core.to.TOFactory;
import kornell.gui.client.ClientFactory;
import kornell.gui.client.ViewFactory;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class AdminCourseClassActivity extends AbstractActivity {
	private ClientFactory clientFactory;

	public AdminCourseClassActivity(ClientFactory clientFactory) {
	    this.clientFactory = clientFactory;
	  }

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		//TODO: unrefernce client factory
		TOFactory toFactory = clientFactory.getTOFactory();
		KornellSession session = clientFactory.getKornellSession();
		PlaceController placeController = clientFactory.getPlaceController();
		Place defaultPlace = clientFactory.getDefaultPlace();
		ViewFactory viewFactory = clientFactory.getViewFactory();
		AdminCourseClassPresenter presenter = viewFactory.getAdminCourseClassPresenter();
		panel.setWidget(presenter);
		
	}

}
