package com.craftware.kornell.client.presenter.vitrine;


import com.craftware.kornell.client.ClientFactory;
import com.google.gwt.user.client.ui.Widget;

public class VitrinePresenter implements VitrineView.Presenter {
	private final ClientFactory clientFactory;

	public VitrinePresenter(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		clientFactory.getVitrineView().setPresenter(this);
	}
	

	@Override
	public Widget asWidget() {
		return getView().asWidget();
	}


	private VitrineView getView() {
		return clientFactory.getVitrineView();
	}

}