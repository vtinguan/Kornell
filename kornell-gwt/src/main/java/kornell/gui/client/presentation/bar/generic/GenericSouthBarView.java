package kornell.gui.client.presentation.bar.generic;

import java.util.logging.Logger;

import kornell.gui.client.ClientFactory;
import kornell.gui.client.event.ClassroomEvent;
import kornell.gui.client.event.ClassroomEventHandler;
import kornell.gui.client.event.HideSouthBarEvent;
import kornell.gui.client.event.HideSouthBarEventHandler;
import kornell.gui.client.presentation.admin.AdminPlace;
import kornell.gui.client.presentation.bar.ActivityBarView;
import kornell.gui.client.presentation.bar.AdminBarView;
import kornell.gui.client.presentation.bar.CourseBarView;
import kornell.gui.client.presentation.bar.SouthBarView;
import kornell.gui.client.presentation.course.ClassroomPlace;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class GenericSouthBarView extends Composite implements
		SouthBarView,
		HideSouthBarEventHandler,
		ClassroomEventHandler {
	private static final Logger log = Logger.getLogger(GenericSouthBarView.class.getName());
	interface MyUiBinder extends UiBinder<Widget, GenericSouthBarView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private AdminBarView adminBarView;

	private PlaceController placeCtrl;

	@UiField
	FlowPanel southBar;

	private ClientFactory clientFactory;


	// TODO: Prefer Dependency Injection
	public GenericSouthBarView(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		EventBus bus = clientFactory.getEventBus();
		bus.addHandler(HideSouthBarEvent.TYPE, this);
		bus.addHandler(ClassroomEvent.TYPE, this);
		initWidget(uiBinder.createAndBindUi(this));

		bus.addHandler(PlaceChangeEvent.TYPE,
				new PlaceChangeEvent.Handler() {
					@Override
					public void onPlaceChange(PlaceChangeEvent event) {
						Place newPlace = event.getNewPlace();
						pickSouthBar(newPlace);
					}
				});
	}

	private void pickSouthBar(Place newPlace) {
		if (newPlace instanceof AdminPlace) {
			southBar.clear();
			// southBar.add(getAdminBarView(newPlace));
			this.setVisible(false);
		}else if (newPlace instanceof ClassroomPlace) {			
			this.setVisible(true);
		} else {
			this.setVisible(false);
		}
	}

	private AdminBarView getAdminBarView(Place newPlace) {
		if (adminBarView == null)
			adminBarView = new GenericAdminBarView(clientFactory);
		return adminBarView;
	}

	@Override
	public void setPresenter(Presenter presenter) {
	}

	@Override
	public void onHideSouthBar(HideSouthBarEvent event) {
		clientFactory.getViewFactory().getDockLayoutPanel()
				.setWidgetHidden((Widget) this, event.isHideSouthBar());
		this.setVisible(!event.isHideSouthBar());
	}

	@Override
	public void onClassroomStarted(ClassroomEvent event) {
		GenericActivityBarView activityBarView = new GenericActivityBarView(clientFactory.getEventBus(),
				clientFactory.getKornellSession(),
				clientFactory.getPlaceController());
		southBar.clear();
		southBar.add(activityBarView);
		southBar.setVisible(true);
	}

}
