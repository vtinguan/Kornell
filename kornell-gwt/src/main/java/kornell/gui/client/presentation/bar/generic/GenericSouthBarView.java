package kornell.gui.client.presentation.bar.generic;

import kornell.gui.client.ClientFactory;
import kornell.gui.client.event.HideSouthBarEvent;
import kornell.gui.client.event.HideSouthBarEventHandler;
import kornell.gui.client.presentation.admin.AdminPlace;
import kornell.gui.client.presentation.bar.ActivityBarView;
import kornell.gui.client.presentation.bar.AdminBarView;
import kornell.gui.client.presentation.bar.SouthBarView;
import kornell.gui.client.presentation.course.ClassroomPlace;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class GenericSouthBarView extends Composite implements SouthBarView, HideSouthBarEventHandler {

	interface MyUiBinder extends UiBinder<Widget, GenericSouthBarView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	private AdminBarView adminBarView;

	@UiField
	FlowPanel southBar;

	private ClientFactory clientFactory;

	private ScrollPanel scrollPanel;

	public GenericSouthBarView(ClientFactory clientFactory, ScrollPanel scrollPanel) {
		this.clientFactory = clientFactory;
		clientFactory.getEventBus().addHandler(HideSouthBarEvent.TYPE,this);
		initWidget(uiBinder.createAndBindUi(this));
		this.scrollPanel = scrollPanel;
		
		pickSouthBar(clientFactory.getPlaceController().getWhere());

		clientFactory.getEventBus().addHandler(PlaceChangeEvent.TYPE, new PlaceChangeEvent.Handler() {
			@Override
			public void onPlaceChange(PlaceChangeEvent event) {
				Place newPlace = event.getNewPlace();
				pickSouthBar(newPlace);
			}
		});
	}

	private void pickSouthBar(Place newPlace) {
		if(newPlace instanceof AdminPlace && clientFactory.getKornellSession().isPlatformAdmin()){
			southBar.clear();
			southBar.add(getAdminBarView(newPlace));
			this.setVisible(true);
			scrollPanel.addStyleName("offsetSouthBar");
		} else if (newPlace instanceof ClassroomPlace) {
			southBar.clear();
			southBar.add(getActivityBarView());
			this.setVisible(true);
			scrollPanel.addStyleName("offsetSouthBar");
		} else {
			this.setVisible(false);
			scrollPanel.removeStyleName("offsetSouthBar");
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
		//clientFactory.getViewFactory().getDockLayoutPanel().setWidgetHidden((Widget) this, event.isHideSouthBar());
		this.setVisible(!event.isHideSouthBar());
  }

}
