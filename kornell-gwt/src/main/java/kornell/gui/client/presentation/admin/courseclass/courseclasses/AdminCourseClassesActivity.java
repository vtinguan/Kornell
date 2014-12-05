package kornell.gui.client.presentation.admin.courseclass.courseclasses;

import kornell.api.client.KornellSession;
import kornell.core.to.TOFactory;
import kornell.gui.client.ClientFactory;
import kornell.gui.client.ViewFactory;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class AdminCourseClassesActivity extends AbstractActivity {
	private ClientFactory clientFactory;

	public AdminCourseClassesActivity(ClientFactory clientFactory) {
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
		AdminCourseClassesPresenter presenter = new AdminCourseClassesPresenter(session,placeController,defaultPlace,toFactory,viewFactory);
		panel.setWidget(presenter);
		
	}

}
