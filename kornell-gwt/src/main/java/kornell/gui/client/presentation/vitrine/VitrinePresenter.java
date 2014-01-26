package kornell.gui.client.presentation.vitrine;


import java.util.ArrayList;
import java.util.List;

import kornell.api.client.Callback;
import kornell.core.entity.Registration;
import kornell.core.to.CourseClassTO;
import kornell.core.to.CourseClassesTO;
import kornell.core.to.RegistrationRequestTO;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.ClientFactory;
import kornell.gui.client.event.LoginEvent;
import kornell.gui.client.presentation.admin.home.AdminHomePlace;
import kornell.gui.client.presentation.course.ClassroomPlace;
import kornell.gui.client.presentation.profile.ProfilePlace;
import kornell.gui.client.presentation.terms.TermsPlace;
import kornell.gui.client.presentation.util.FormHelper;
import kornell.gui.client.presentation.util.KornellNotification;
import kornell.gui.client.util.ClientProperties;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;

public class VitrinePresenter implements VitrineView.Presenter {
	private final ClientFactory clientFactory;
	private VitrineView view;
	private String passwordChangeUUID;

	public VitrinePresenter(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		view = getView();
		view.setPresenter(this);
		
		this.passwordChangeUUID = ((VitrinePlace) clientFactory.getPlaceController().getWhere()).getPasswordChangeUUID();
		if(passwordChangeUUID != null && !"".equals(passwordChangeUUID)){
			view.displayView(VitrineViewType.newPassword);
		}
		
		String assetsURL = clientFactory.getInstitution().getAssetsURL();
		view.setLogoURL(assetsURL);
		view.setBackgroundImage(assetsURL);
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
			public void ok(final UserInfoTO user) {
				clientFactory.getUserSession().setCurrentUser(user);
				clientFactory.getEventBus().fireEvent(new LoginEvent(user));
				//if("".equals(user.getPerson().getConfirmation())){
					doLogin(user);
				/*} else {
					view.setMessage("Usuário não verificado. Confira seu email.");
					view.showMessage();
					ClientProperties.remove("Authorization");
				}*/
			}

			private void doLogin(final UserInfoTO user) {
				clientFactory.getUserSession().getCourseClassesTO(new Callback<CourseClassesTO>(){
					@Override
					public void ok(CourseClassesTO courseClasses) {
						for (CourseClassTO courseClassTmp : courseClasses.getCourseClasses()) {
							if(courseClassTmp.getCourseClass().getInstitutionUUID().equals(clientFactory.getInstitution().getUUID())){
								clientFactory.setCurrentCourse(courseClassTmp);
								clientFactory.setDefaultPlace(new ClassroomPlace(courseClassTmp.getCourseClass().getUUID()));
							}
						}
						boolean isRegistered = false;
						for (Registration registration : clientFactory.getUserSession().getUserInfo().getRegistrationsTO().getRegistrations()) {
							if(registration.getInstitutionUUID().equals(clientFactory.getInstitution().getUUID()))
								isRegistered = true;
						}
						if(!isRegistered){
							view.setMessage("Usuário não registrado nesta instituição.");
							view.showMessage();
							ClientProperties.remove("Authorization");
						} else {
							clientFactory.getEventBus().fireEvent(new LoginEvent(user));
							if(user.isSigningNeeded()){
								clientFactory.getPlaceController().goTo(new TermsPlace());
							} else if(clientFactory.getInstitution().isDemandsPersonContactDetails() && 
									clientFactory.getUserSession().getUserInfo().getPerson().getCity() == null){
								clientFactory.getPlaceController().goTo(new ProfilePlace(clientFactory.getUserSession().getUserInfo().getPerson().getUUID(), true));
							} else {
								//TODO what if the user visited a class from another institution?
								//place = clientFactory.getHistoryMapper().getPlace(user.getLastPlaceVisited());
								if(clientFactory.getUserSession().isDean()){
									clientFactory.getPlaceController().goTo(new AdminHomePlace());
								} else {
									clientFactory.getPlaceController().goTo(clientFactory.getDefaultPlace());
								}
							}
						}
					}			
				});	
			}

			@Override
			protected void unauthorized() {
				view.setMessage("Usuário ou senha incorretos, por favor tente novamente.");
				view.showMessage();
			}
		};
		String confirmation = "";//((VitrinePlace)clientFactory.getPlaceController().getWhere()).getConfirmation();
		//GWT.log("Confirmation: " + confirmation);
		clientFactory.getUserSession().login(view.getEmail().toLowerCase().trim(),
				view.getPassword(),
				confirmation,
				callback);
	}

	@Override
	public void onRegisterButtonClicked() {
		view.hideMessage();
		view.displayView(VitrineViewType.register);		
	}

	@Override
	public void onForgotPasswordButtonClicked() {
		view.hideMessage();
		view.displayView(VitrineViewType.forgotPassword);
	}

	private List<String> validateFields() {
		FormHelper validator = new FormHelper();
		List<String> errors = new ArrayList<String>();
		
		if(!validator.isLengthValid(view.getSuName(), 2, 50)){
			errors.add("O nome deve ter no mínimo 2 caracteres.");
		}
		
		if (!validator.isEmailValid(view.getSuEmail())){
			errors.add("Email inválido.");
		}

		if (!validator.isPasswordValid(view.getSuPassword())){
			errors.add("Senha inválida (mínimo de 6 caracteres).");
		}

		if (!view.getSuPassword().equals(view.getSuPasswordConfirm())){
			errors.add("As senhas não conferem.");
		}
		return errors;
	}
	
	@Override
	public void onSignUpButtonClicked() {
		view.hideMessage();
		List<String> errors = validateFields();
		
		if(errors.size() == 0){
			//TODO improve
			clientFactory.getKornellClient().checkUser(view.getSuEmail().toLowerCase().trim(), new Callback<UserInfoTO>(){
				@Override
				public void ok(UserInfoTO user){
					if(user.getEmail() != null){
						view.setMessage("O email já existe.");
						view.showMessage();
						return;
					}
					RegistrationRequestTO registrationRequestTO = buildRegistrationRequestTO();
					clientFactory.getKornellClient().requestRegistration(registrationRequestTO, new Callback<UserInfoTO>(){
						@Override
						public void ok(UserInfoTO user){
							GWT.log("User created");
							KornellNotification.show("Usuário criado com sucesso."
									/*+ " Uma solicitação foi enviada para a instituição requisitando seu acesso. Você receberá um email quando a requisição for aprovada."*/
									, 2000);
							view.displayView(VitrineViewType.login);
							view.setEmail(view.getSuEmail());
							view.setPassword(view.getSuPassword());
							doLogin();
						}
					});
				}

				private RegistrationRequestTO buildRegistrationRequestTO() {
					RegistrationRequestTO registrationRequestTO = clientFactory.getTOFactory().newRegistrationRequestTO().as();
					registrationRequestTO.setFullName(view.getSuName().trim());
					registrationRequestTO.setEmail(view.getSuEmail().toLowerCase().trim());
					registrationRequestTO.setPassword(view.getSuPassword());
					registrationRequestTO.setInstitutionUUID(clientFactory.getInstitution().getUUID());
					return registrationRequestTO;
				}
			});
		} else {
			view.setMessage(errors);
			view.showMessage();
		}
		
	}

	@Override
	public void onCancelSignUpButtonClicked() {
		view.hideMessage();
		view.displayView(VitrineViewType.login);		
	}

	@Override
	public void onRequestPasswordChangeButtonClicked() {
		clientFactory.getKornellClient().requestPasswordChange(view.getFpEmail().toLowerCase().trim(), clientFactory.getInstitution().getName(), new Callback<Void>() {
			@Override
			public void ok(Void to) {
				view.displayView(VitrineViewType.login);	
				KornellNotification.show("Requisição feita. Confira seu e-mail.");
			}
			@Override
			public void unauthorized() {	
				KornellNotification.show("Não foi possivel fazer a requisição. Confira se o seu email foi digitado corretamente.", AlertType.ERROR);
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
		
		if (!validator.isPasswordValid(view.getNewPassword())){
			errors.add("Senha inválida (mínimo de 6 caracteres).");
		}
		if (!view.getNewPassword().equals(view.getNewPasswordConfirm())){
			errors.add("As senhas não conferem.");
		}
		if(errors.size() == 0){
			clientFactory.getKornellClient().changePassword(view.getNewPassword(), passwordChangeUUID, new Callback<UserInfoTO>() {
				@Override
				public void ok(UserInfoTO to) {
					view.displayView(VitrineViewType.login);	
					KornellNotification.show("Senha alterada com sucesso.");
					view.setEmail(to.getEmail());
					view.setPassword(view.getNewPassword());
					doLogin();
				}
				
				@Override
				public void unauthorized(){
					KornellNotification.show("Não foi possível alterar a senha. Verifique seu email ou faça uma nova requisição de alteração de senha.", AlertType.ERROR, 8000);
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