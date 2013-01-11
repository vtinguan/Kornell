package kornell.gui.client.presentation.vitrine.generic;

import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.api.client.data.Person;
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
import com.google.gwt.place.shared.PlaceController;
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

	public GenericVitrineView(
			PlaceController placeCtrl,
			KornellClient client) {
		this.placeCtrl = placeCtrl;
		this.client = client;
	
		initWidget(uiBinder.createAndBindUi(this));
		pwdPassword.addKeyPressHandler(new KeyPressHandler() {			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(KeyCodes.KEY_ENTER == event.getCharCode())
					doLogin();				
			}
		});
		
		
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
		Callback callback = new Callback() {
			@Override
			protected void ok(Person person) {
				placeCtrl.goTo(new WelcomePlace());
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
