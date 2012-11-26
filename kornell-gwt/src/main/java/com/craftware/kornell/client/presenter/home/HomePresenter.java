package com.craftware.kornell.client.presenter.home;


import com.craftware.kornell.client.ClientFactory;
import com.google.gwt.user.client.ui.Widget;

public class HomePresenter implements HomeView.Presenter {
	private final ClientFactory clientFactory;

	public HomePresenter(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		clientFactory.getHomeView().setPresenter(this);
	}
	

	@Override
	public Widget asWidget() {
		return getView().asWidget();
	}


	private HomeView getView() {
		return clientFactory.getHomeView();
	}

}