package kornell.gui.client.presentation.terms.generic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.api.client.UserSession;
import kornell.core.entity.Institution;
import kornell.core.entity.Person;
import kornell.core.entity.Registration;
import kornell.core.to.RegistrationsTO;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.event.LogoutEvent;
import kornell.gui.client.presentation.course.CourseClassPlace;
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

	Registration registration;
	Institution institution;
	UserInfoTO user;

	private UserSession session;

	private PlaceController placeCtrl;
	private Place defaultPlace;

	private String barLogoFileName = "logo300x80.png";

	private KornellConstants constants = GWT.create(KornellConstants.class);

	private GenericMenuLeftView menuLeftView;

	private final EventBus bus;

	public GenericTermsView(EventBus bus, UserSession session,
			PlaceController placeCtrl, Place defaultPlace, Institution institution) {
		this.bus = bus;
		this.session = session;
		this.placeCtrl = placeCtrl;
		this.defaultPlace = defaultPlace;
		this.institution = institution;
		initWidget(uiBinder.createAndBindUi(this));
		initData();
		// TODO i18n
		txtTitle.setText("Termos de Uso".toUpperCase());
		txtTerms.setText("[Carregando, aguarde...]");
		btnAgree.setText("Concordo".toUpperCase());
		btnDontAgree.setText("Não Concordo".toUpperCase());
	}

	private void initData() {
		user = session.getUserInfo();
		for (Registration registration : user.getRegistrationsTO().getRegistrations()) {
			if(institution.getUUID().equals(registration.getInstitutionUUID()) && registration.getTermsAcceptedOn() != null){
				GWT.log("OPS! Should not be here if there's nothing to sign.");
				goStudy();
			}
		}
		paint();
	}

	private void paint() {
		Person p = user.getPerson();
		titleUser.setText(p.getFullName());
		if (institution != null) {
			txtTerms.getElement().setInnerHTML(institution.getTerms());
			institutionLogo.setUrl(institution.getAssetsURL() + barLogoFileName);
		}
	}

	@UiHandler("btnAgree")
	void handleClickAll(ClickEvent e) {
		session.institution(institution.getUUID()).acceptTerms(
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
		placeCtrl.goTo(defaultPlace);
	}

}