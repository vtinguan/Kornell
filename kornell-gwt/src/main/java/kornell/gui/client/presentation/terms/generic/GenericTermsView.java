package kornell.gui.client.presentation.terms.generic;

import kornell.api.client.Callback;
import kornell.api.client.UserSession;
import kornell.core.entity.Institution;
import kornell.core.entity.Person;
import kornell.core.entity.Registration;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.ClientFactory;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.event.LogoutEvent;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.presentation.profile.ProfilePlace;
import kornell.gui.client.presentation.terms.TermsView;
import kornell.gui.client.presentation.welcome.generic.GenericMenuLeftView;

import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class GenericTermsView extends Composite implements TermsView {
	interface MyUiBinder extends UiBinder<Widget, GenericTermsView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	Paragraph titleUser;
	@UiField
	Paragraph txtTitle;
	@UiField
	Paragraph txtTerms;
	@UiField
	Button btnAgree;
	@UiField
	Button btnDontAgree;
	@UiField
	Image institutionLogo;

	private ClientFactory clientFactory;
	private UserSession session;
	private PlaceController placeCtrl;
	private EventBus bus;
	private KornellConstants constants = GWT.create(KornellConstants.class);

	private String barLogoFileName = "logo300x80.png";


	public GenericTermsView(ClientFactory clientFactory) {
		this.bus = clientFactory.getEventBus();
		this.session = clientFactory.getUserSession();
		this.placeCtrl = clientFactory.getPlaceController();
		this.clientFactory = clientFactory;
		initWidget(uiBinder.createAndBindUi(this));
		initData();
		// TODO i18n
		txtTitle.setText("Termos de Uso".toUpperCase());
		btnAgree.setText("Concordo".toUpperCase());
		btnDontAgree.setText("Não Concordo".toUpperCase());
	}

	private void initData() {
		for (Registration registration : session.getUserInfo().getRegistrationsTO().getRegistrations()) {
			if(Dean.getInstance().getInstitution().getUUID().equals(registration.getInstitutionUUID()) && registration.getTermsAcceptedOn() != null){
				GWT.log("OPS! Should not be here if there's nothing to sign.");
				goStudy();
			}
		}
		paint();
	}

	private void paint() {
		titleUser.setText(session.getUserInfo().getPerson().getFullName());
		if (Dean.getInstance().getInstitution() != null) {
			txtTerms.getElement().setInnerHTML(Dean.getInstance().getInstitution().getTerms());
			institutionLogo.setUrl(Dean.getInstance().getInstitution().getAssetsURL() + barLogoFileName);
		}
	}

	@UiHandler("btnAgree")
	void handleClickAll(ClickEvent e) {
		session.institution(Dean.getInstance().getInstitution().getUUID()).acceptTerms(
				new Callback<Void>() {
					@Override
					public void ok(Void v) {
						goStudy();
					}
				});
	}

	@UiHandler("btnDontAgree")
	void handleClickInProgress(ClickEvent e) {
		bus.fireEvent(new LogoutEvent());
	}

	@Override
	public void setPresenter(Presenter presenter) {
	}

	private void goStudy() {
		if(Dean.getInstance().getInstitution().isDemandsPersonContactDetails()){
			placeCtrl.goTo(new ProfilePlace(session.getUserInfo().getPerson().getUUID(), true));
		} else {
			placeCtrl.goTo(clientFactory.getDefaultPlace());
		}
	}

}