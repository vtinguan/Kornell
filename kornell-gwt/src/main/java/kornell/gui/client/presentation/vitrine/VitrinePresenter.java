package kornell.gui.client.presentation.vitrine;


import kornell.api.client.Callback;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.ClientFactory;
import kornell.gui.client.event.LoginEvent;
import kornell.gui.client.presentation.profile.ProfilePlace;
import kornell.gui.client.presentation.terms.TermsPlace;
import kornell.gui.client.util.ClientProperties;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.Widget;

public class VitrinePresenter implements VitrineView.Presenter {
	private final ClientFactory clientFactory;
	private VitrineView view;

	public VitrinePresenter(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		view = getView();
		view.setPresenter(this);
		checkIfUserWasCreated();
	}

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}


	private VitrineView getView() {
		return clientFactory.getVitrineView();
	}

	@Override
	public void onLoginButtonClicked() {
		view.showMessage();
		doLogin();
	}


	private void doLogin() {
		view.hideMessage();
		Callback<UserInfoTO> callback = new Callback<UserInfoTO>() {
			@Override
			protected void ok(UserInfoTO user) {
				clientFactory.getEventBus().fireEvent(new LoginEvent(user));
				if("".equals(user.getPerson().getConfirmation())){
					if(user.isSigningNeeded()){
						clientFactory.getPlaceController().goTo(new TermsPlace());
					} else {
						String token = user.getLastPlaceVisited();
						Place place;
						if(token == null || token.contains("vitrine")){
							place = clientFactory.getDefaultPlace();
						}else {
							place = clientFactory.getHistoryMapper().getPlace(token);
						}
						clientFactory.getEventBus().fireEvent(new LoginEvent(user));
						clientFactory.getPlaceController().goTo(place);
					}
				} else {
					view.setMessage("Usuário não verificado. Confira seu email.");
					view.showMessage();
					ClientProperties.remove("Authorization");
				}
			}

			@Override
			protected void unauthorized() {
				view.setMessage("Usuário ou senha incorretos, por favor tente novamente.");
				view.showMessage();
			}
		};
		String confirmation = ((VitrinePlace)clientFactory.getPlaceController().getWhere()).getConfirmation();
		GWT.log("Confirmation: " + confirmation);
		clientFactory.getKornellClient().login(view.getUsername().toLowerCase().trim(),
				view.getPassword(),
				confirmation,
				callback);
	}

	@Override
	public void onRegisterButtonClicked() {
		clientFactory.getPlaceController().goTo(new ProfilePlace(""));
	}

	@Override
	public void checkIfUserWasCreated() {
		if (((VitrinePlace) clientFactory.getPlaceController().getWhere()).isUserCreated()) {
			view.showUserCreatedAlert();
			((VitrinePlace) clientFactory.getPlaceController().getWhere()).setUserCreated(false);
		}
	}

}