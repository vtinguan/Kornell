package kornell.gui.client.presentation.vitrine.generic;

import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.core.shared.to.UserInfoTO;
import kornell.gui.client.presentation.terms.TermsPlace;
import kornell.gui.client.presentation.vitrine.VitrineView;
import kornell.gui.client.presentation.welcome.WelcomePlace;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.PasswordTextBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

//HTTP

public class GenericVitrineView extends Composite implements VitrineView {
	interface MyUiBinder extends UiBinder<Widget, GenericVitrineView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private PlaceController placeCtrl;

	
	@UiField
	TextBox txtUsername;
	@UiField
	PasswordTextBox pwdPassword;
	@UiField
	Form frmLogin;

	@UiField
	Alert altUnauthorized;

	@UiField
	FlowPanel contentPanel;


	private KornellClient client;
	private Place defaultPlace;
	private PlaceHistoryMapper mapper;
	//TODO i18n xml
	public GenericVitrineView(
			PlaceHistoryMapper mapper, 
			PlaceController placeCtrl,
			Place defaultPlace,
			KornellClient client) {
		this.placeCtrl = placeCtrl;
		this.client = client;
		this.defaultPlace = defaultPlace;
		this.mapper = mapper;
	
		initWidget(uiBinder.createAndBindUi(this));
		pwdPassword.addKeyPressHandler(new KeyPressHandler() {			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(KeyCodes.KEY_ENTER == event.getCharCode())
					doLogin();				
			}
		});
		txtUsername.getElement().setAttribute("autocorrect", "off");
		txtUsername.getElement().setAttribute("autocapitalize", "off");
		
	}


	@Override
	public void setPresenter(Presenter presenter) {
	}

	@UiHandler("btnLogin")
	void doLogin(ClickEvent e) {
		doLogin();
	}

	private void doLogin() {
		altUnauthorized.setVisible(false);
		Callback<UserInfoTO> callback = new Callback<UserInfoTO>() {
			@Override
			protected void ok(UserInfoTO user) {
				//TODO person signed terms of use
				if(user.isSigningNeeded()){
					placeCtrl.goTo(new TermsPlace());
				} else {
					String token = user.getLastPlaceVisited();
					Place place;
					if(token != null){
						place = mapper.getPlace(token);
					}else {
						place = defaultPlace;
					}
					placeCtrl.goTo(place);
				}
			}

			@Override
			protected void unauthorized() {
				altUnauthorized.setVisible(true);
			}
		};
		// TODO: Should be client.auth().checkPassword()?
		// TODO: Should the api accept HasValue<String> too?
		client.login(txtUsername.getValue(),
				pwdPassword.getValue(),
				callback);
	}

}
