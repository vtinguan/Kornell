package kornell.gui.client.presentation.vitrine;

import java.util.ArrayList;
import java.util.List;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.Institution;
import kornell.core.to.CourseClassesTO;
import kornell.core.to.RegistrationRequestTO;
import kornell.core.to.UserInfoTO;
import kornell.core.util.StringUtils;
import kornell.gui.client.ClientFactory;
import kornell.gui.client.event.LoginEvent;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.presentation.admin.home.AdminHomePlace;
import kornell.gui.client.presentation.profile.ProfilePlace;
import kornell.gui.client.presentation.terms.TermsPlace;
import kornell.gui.client.presentation.util.FormHelper;
import kornell.gui.client.presentation.util.KornellNotification;
import kornell.gui.client.presentation.welcome.WelcomePlace;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.Widget;

public class VitrinePresenter implements VitrineView.Presenter {
	private final ClientFactory clientFactory;
	private VitrineView view;
	private String passwordChangeUUID;
	private KornellSession session;
	private String registrationEmail;

	public VitrinePresenter(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		this.session = clientFactory.getKornellSession();
		view = getView();
		view.setPresenter(this);
		

		if(clientFactory.getPlaceController().getWhere() instanceof VitrinePlace){
			VitrinePlace place = ((VitrinePlace) clientFactory.getPlaceController().getWhere());
			this.registrationEmail = place.getEmail();
			if (StringUtils.isSome(registrationEmail)) {
				view.setRegistrationEmail(registrationEmail);
				view.displayView(VitrineViewType.register);
			} else if(StringUtils.isSome(place.getPasswordChangeUUID())){
				view.displayView(VitrineViewType.newPassword);
			}
		}

		Dean localdean = Dean.getInstance();
		if (localdean != null) {
			String assetsURL = localdean.getInstitution().getAssetsURL();
			view.setLogoURL(assetsURL);
			view.showRegistrationOption(localdean.getInstitution().isAllowRegistration());
		}
	}

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	private VitrineView getView() {
		return clientFactory.getViewFactory().getVitrineView();
	}

	@Override
	public void onLoginButtonClicked() {
		view.showMessage();
		doLogin();
	}

	private void doLogin() {
		view.hideMessage();
		
		final Institution institution = Dean.getInstance().getInstitution();
		
		final Callback<CourseClassesTO> courseClassesCallback = new Callback<CourseClassesTO>() {
			@Override
			public void ok(CourseClassesTO courseClasses) {
				Dean.getInstance().setCourseClassesTO(courseClasses);
				final UserInfoTO userInfoTO = session.getCurrentUser();
				clientFactory.setDefaultPlace(new WelcomePlace());
				clientFactory.getEventBus().fireEvent(new LoginEvent(userInfoTO));
				Place newPlace;
				Place welcomePlace = new WelcomePlace();
				if (StringUtils.isSome(institution.getTerms()) && !session.hasSignedTerms()) {
					 newPlace = new TermsPlace();
				} else if (institution.isDemandsPersonContactDetails() && userInfoTO.getPerson().getCity() == null) {
					newPlace = new ProfilePlace(userInfoTO.getPerson().getUUID(), true);
				} else if (session.isInstitutionAdmin()) {
					newPlace = new AdminHomePlace();
				} else {
					newPlace = welcomePlace;
				}
				clientFactory.setDefaultPlace(newPlace instanceof AdminHomePlace ? newPlace : welcomePlace);
				clientFactory.getPlaceController().goTo(newPlace);
			}
		};
		
		Callback<UserInfoTO> userInfoCallback = new Callback<UserInfoTO>() {
			@Override
			public void ok(final UserInfoTO user) {
				view.displayView(null);
				clientFactory.getEventBus().fireEvent(new LoginEvent(user));
				session.courseClasses().getCourseClassesTOByInstitution(institution.getUUID(), courseClassesCallback);
			}
			@Override
			protected void unauthorized(String errorMessage) {
				GWT.log(this.getClass().getName() + " - " + errorMessage);
				view.setMessage("Usuário ou senha incorretos, por favor tente novamente.");
				view.showMessage();
			}
		};
		String email = view.getEmail().toLowerCase().trim(); 
    String password = view.getPassword();
    session.login(email, password, userInfoCallback);
	}

	@Override
	public void onRegisterButtonClicked() {
		if(Dean.getInstance().getInstitution().isAllowRegistration()){
			view.hideMessage();
			view.displayView(VitrineViewType.register);
		}
	}

	@Override
	public void onForgotPasswordButtonClicked() {
		view.hideMessage();
		view.displayView(VitrineViewType.forgotPassword);
	}

	private List<String> validateFields() {
		FormHelper validator = new FormHelper();
		List<String> errors = new ArrayList<String>();
		if (!validator.isLengthValid(view.getSuName(), 2, 50)) {
			errors.add("O nome deve ter no mínimo 2 caracteres.");
		}
		if (!validator.isEmailValid(view.getSuEmail())) {
			errors.add("Email inválido.");
		}
		if (!validator.isPasswordValid(view.getSuPassword())) {
			errors.add("Senha inválida (mínimo de 6 caracteres).");
		}
		if (view.getSuPassword().indexOf(':') >= 0) {
			errors.add("Senha inválida (não pode conter o caractere ':').");
		}
		if (!view.getSuPassword().equals(view.getSuPasswordConfirm())) {
			errors.add("As senhas não conferem.");
		}
		return errors;
	}

	@Override
	public void onSignUpButtonClicked() {
		if(StringUtils.isSome(registrationEmail) || Dean.getInstance().getInstitution().isAllowRegistration()){
			view.hideMessage();
			List<String> errors = validateFields();
			if (errors.size() == 0) {
				signUp();
			} else {
				view.setMessage(errors);
				view.showMessage();
			}
		}
	}

	private void signUp() {
		final String suName = view.getSuName().trim();
		final String suEmail = view.getSuEmail().toLowerCase().trim();
		final String suPassword = view.getSuPassword();
		final Callback<UserInfoTO> registrationCallback = new Callback<UserInfoTO>() {
			@Override
			public void ok(UserInfoTO user) {
				GWT.log("User created");
				KornellNotification.show("Usuário criado com sucesso.", 2000);
				view.displayView(VitrineViewType.login);
				view.setEmail(view.getSuEmail());
				view.setPassword(suPassword);
				doLogin();
			}
		};
		// TODO improve
		Callback<UserInfoTO> checkUserCallback = new Callback<UserInfoTO>() {
			@Override
			public void ok(UserInfoTO user) {
				if (user.getUsername() != null) {
					view.setMessage("O email já existe.");
					view.showMessage();
					return;
				}
				RegistrationRequestTO registrationRequestTO = buildRegistrationRequestTO(suName, suEmail, suPassword);
				session.user().requestRegistration(registrationRequestTO, registrationCallback);
			}
		};
		session.user().checkUser(Dean.getInstance().getInstitution().getUUID(), suEmail, checkUserCallback);
	}

	private RegistrationRequestTO buildRegistrationRequestTO(String name, String email, String password) {
		RegistrationRequestTO registrationRequestTO = clientFactory.getTOFactory().newRegistrationRequestTO().as();
		registrationRequestTO.setFullName(name);
		registrationRequestTO.setEmail(email);
		registrationRequestTO.setPassword(password);
		registrationRequestTO.setInstitutionUUID(Dean.getInstance().getInstitution().getUUID());
		return registrationRequestTO;
	}

	@Override
	public void onCancelSignUpButtonClicked() {
		view.hideMessage();
		view.displayView(VitrineViewType.login);
	}

	@Override
	public void onRequestPasswordChangeButtonClicked() {
		session.user().requestPasswordChange(
				view.getFpEmail().toLowerCase().trim(),
				Dean.getInstance().getInstitution().getName(),
				new Callback<Void>() {
					@Override
					public void ok(Void to) {
						view.displayView(VitrineViewType.login);
						KornellNotification
								.show("Requisição feita. Confira seu e-mail.");
					}

					@Override
					public void unauthorized(String errorMessage) {
						GWT.log(this.getClass().getName() + " - " + errorMessage);
						KornellNotification
								.show("Não foi possivel fazer a requisição. Confira se o seu email foi digitado corretamente.",
										AlertType.ERROR);
					}
				});
	}

	@Override
	public void onCancelPasswordChangeRequestButtonClicked() {
		view.hideMessage();
		view.displayView(VitrineViewType.login);
	}

	@Override
	public void onChangePasswordButtonClicked() {
		view.hideMessage();
		FormHelper validator = new FormHelper();
		List<String> errors = new ArrayList<String>();

		if (!validator.isPasswordValid(view.getNewPassword())) {
			errors.add("Senha inválida (mínimo de 6 caracteres).");
		}
		if (view.getNewPassword().indexOf(':') >= 0) {
			errors.add("Senha inválida (não pode conter o caractere ':').");
		}
		if (!view.getNewPassword().equals(view.getNewPasswordConfirm())) {
			errors.add("As senhas não conferem.");
		}
		if (errors.size() == 0) {
			session.user().changePassword(view.getNewPassword(), passwordChangeUUID,
					new Callback<UserInfoTO>() {
						@Override
						public void ok(UserInfoTO to) {
							view.displayView(VitrineViewType.login);
							KornellNotification.show("Senha alterada com sucesso.");
							view.setEmail(to.getUsername());
							view.setPassword(view.getNewPassword());
							doLogin();
						}

						@Override
						public void unauthorized(String errorMessage) {
							GWT.log(this.getClass().getName() + " - " + errorMessage);
							KornellNotification
									.show("Não foi possível alterar a senha. Verifique seu email ou faça uma nova requisição de alteração de senha.",
											AlertType.ERROR, 8000);
						}
					});
		} else {
			view.setMessage(errors);
			view.showMessage();
		}
	}

	@Override
	public void onCancelChangePasswordButtonClicked() {
		view.hideMessage();
		view.displayView(VitrineViewType.login);
	}

}