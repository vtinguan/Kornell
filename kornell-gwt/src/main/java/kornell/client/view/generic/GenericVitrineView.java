package kornell.client.view.generic;

import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.client.presenter.home.HomePlace;
import kornell.client.presenter.vitrine.VitrineView;
import kornell.client.ui.InputText;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

//HTTP

public class GenericVitrineView extends Composite implements VitrineView {
	interface MyUiBinder extends UiBinder<Widget, GenericVitrineView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private PlaceController placeCtrl;

	@UiField
	InputText txtPassword;
	@UiField
	InputText txtUsername;

	public GenericVitrineView(PlaceController placeCtrl) {
		this.placeCtrl = placeCtrl;
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(Presenter presenter) {

	}

	@UiHandler("btnLogin")
	void doLogin(ClickEvent e) {
		String url = "http://api.kornell.localdomain:8080";
		KornellClient client = new KornellClient(url);

		RequestCallback callback = new Callback() {
			@Override
			public void ok() {
				Window.alert("All Right, authenticated! Get ready for a new page..");
				placeCtrl.goTo(new HomePlace());
			}

			@Override
			protected void unauthorized(){
				Window.alert("Sorry. Try again");
			}
		};
		// TODO: Should be client.auth().checkPassword()?
		// TODO: Should the api accept HasValue<String> too?
		client.checkPassword(txtUsername.getValue(),
				txtPassword.getValue(),
				callback);

	}
}
