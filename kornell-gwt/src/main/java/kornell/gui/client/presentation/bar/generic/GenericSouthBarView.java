package kornell.gui.client.presentation.bar.generic;

import kornell.gui.client.ClientFactory;
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

public class GenericSouthBarView extends Composite implements SouthBarView, HideSouthBarEventHandler {

	interface MyUiBinder extends UiBinder<Widget, GenericSouthBarView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	private AdminBarView adminBarView;

	private PlaceController placeCtrl;

	@UiField
	FlowPanel southBar;

	private ClientFactory clientFactory;

	public GenericSouthBarView(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		clientFactory.getEventBus().addHandler(HideSouthBarEvent.TYPE,this);
		initWidget(uiBinder.createAndBindUi(this));

		clientFactory.getEventBus().addHandler(PlaceChangeEvent.TYPE, new PlaceChangeEvent.Handler() {
			@Override
			public void onPlaceChange(PlaceChangeEvent event) {
				Place newPlace = event.getNewPlace();
				pickSouthBar(newPlace);
			}
		});
	}

	private void pickSouthBar(Place newPlace) {
		if(newPlace instanceof AdminPlace){
			southBar.clear();
			//southBar.add(getAdminBarView(newPlace));
			this.setVisible(false);
		} else if (newPlace instanceof ClassroomPlace) {
			southBar.clear();
			southBar.add(getActivityBarView());
			this.setVisible(true);
		} else {
			this.setVisible(false);
		}
	}

	private ActivityBarView getActivityBarView() {
		return new GenericActivityBarView(clientFactory);
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
		clientFactory.getViewFactory().getDockLayoutPanel().setWidgetHidden((Widget) this, event.isHideSouthBar());
		this.setVisible(!event.isHideSouthBar());
  }

}
