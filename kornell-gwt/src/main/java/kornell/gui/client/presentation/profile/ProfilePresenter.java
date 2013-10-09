package kornell.gui.client.presentation.profile;

import kornell.gui.client.ClientFactory;

import com.google.gwt.user.client.ui.Widget;

public class ProfilePresenter implements ProfileView.Presenter{
	private ProfileView view;

	public ProfilePresenter(ClientFactory clientFactory) {
		view = clientFactory.getProfileView();
		view.setPresenter(this);
	}
	
	@Override
	public Widget asWidget() {
		Widget ProfileView = getView().asWidget();
		return ProfileView;
	}
	
	private ProfileView getView() {
		return view;
	}

}
