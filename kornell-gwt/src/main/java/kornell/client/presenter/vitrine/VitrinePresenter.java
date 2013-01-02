package kornell.client.presenter.vitrine;


import kornell.client.ClientFactory;

import com.google.gwt.user.client.ui.Widget;

public class VitrinePresenter implements VitrineView.Presenter {
	private final ClientFactory clientFactory;
	private VitrineView view;

	public VitrinePresenter(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		view = getView();
		getView().setPresenter(this);
	}
	

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}


	private VitrineView getView() {
		return clientFactory.getVitrineView();
	}

}