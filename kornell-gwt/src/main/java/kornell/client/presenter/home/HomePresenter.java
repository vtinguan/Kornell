package kornell.client.presenter.home;


import kornell.client.ClientFactory;

import com.google.gwt.user.client.ui.Widget;

public class HomePresenter implements HomeView.Presenter {
	private final ClientFactory clientFactory;
	HomeView homeView;
	public HomePresenter(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		homeView = clientFactory.getHomeView();
		homeView.setPresenter(this);
	}
	

	@Override
	public Widget asWidget() {
		return homeView.asWidget();
	}


	private HomeView getView() {
		return clientFactory.getHomeView();
	}

}