package kornell.gui.client.presentation.home;


import kornell.gui.client.ClientFactory;

import com.google.gwt.user.client.ui.Widget;

public class HomePresenter implements HomeView.Presenter {
	HomeView homeView;
	public HomePresenter(ClientFactory clientFactory) {
		homeView = clientFactory.getHomeView();
		homeView.setPresenter(this);
	}
	

	@Override
	public Widget asWidget() {
		return homeView.asWidget();
	}
}