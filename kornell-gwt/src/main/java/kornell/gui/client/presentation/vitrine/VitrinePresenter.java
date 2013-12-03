package kornell.gui.client.presentation.vitrine;


import java.util.ArrayList;
import java.util.List;

import kornell.api.client.Callback;
import kornell.core.to.CourseClassTO;
import kornell.core.to.CourseClassesTO;
import kornell.core.to.RegistrationRequestTO;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.ClientFactory;
import kornell.gui.client.event.LoginEvent;
import kornell.gui.client.presentation.course.CourseClassPlace;
import kornell.gui.client.presentation.terms.TermsPlace;
import kornell.gui.client.presentation.util.ValidatorHelper;
import kornell.gui.client.util.ClientProperties;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class VitrinePresenter implements VitrineView.Presenter {
	private final ClientFactory clientFactory;
	private VitrineView view;

	public VitrinePresenter(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		view = getView();
		view.setPresenter(this);
		checkIfUserWasCreated();
		String imgLogoURL = clientFactory.getInstitution().getAssetsURL();
		view.setLogoURL(imgLogoURL);
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
				if("".equals(user.getPerson().getConfirmation())){
					doLogin(user);
				} else {
					view.setMessage("Usuário não verificado. Confira seu email.");
					view.showMessage();
					ClientProperties.remove("Authorization");
				}
			}

			private void doLogin(final UserInfoTO user) {
				clientFactory.getUserSession().getCourseClassesTO(new Callback<CourseClassesTO>(){
					@Override
					public void ok(CourseClassesTO courseClasses) {
						CourseClassTO courseClass = null;
						for (CourseClassTO courseClassTmp : courseClasses.getCourseClasses()) {
							if(courseClassTmp.getCourseClass().getInstitutionUUID().equals(clientFactory.getInstitution().getUUID())){
								courseClass = courseClassTmp;
							}
						}
						clientFactory.setCurrentCourse(courseClass);
						clientFactory.setDefaultPlace(new CourseClassPlace(courseClass.getCourseClass().getUUID()));
						
						if(!clientFactory.getUserSession().isRegistered()){
							view.setMessage("Usuário não registrado nesta instituição.");
							view.showMessage();
							ClientProperties.remove("Authorization");
						} else if(user.isSigningNeeded()){
							clientFactory.getPlaceController().goTo(new TermsPlace());
						} else {
							//TODO what if the user visited a class from another institution?
							String token = null;//user.getLastPlaceVisited();
							Place place;
							if(token == null || token.contains("vitrine")){
								place = clientFactory.getDefaultPlace();
							}else {
								place = clientFactory.getHistoryMapper().getPlace(token);
							}
							clientFactory.getEventBus().fireEvent(new LoginEvent(user));
							clientFactory.getPlaceController().goTo(place);
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
		String confirmation = ((VitrinePlace)clientFactory.getPlaceController().getWhere()).getConfirmation();
		GWT.log("Confirmation: " + confirmation);
		clientFactory.getUserSession().login(view.getUsername().toLowerCase().trim(),
				view.getPassword(),
				confirmation,
				callback);
	}

	@Override
	public void onRegisterButtonClicked() {
		view.hideMessage();
		view.displayLoginPanel(false);		
	}

	private List<String> validateFields() {
		ValidatorHelper validator = new ValidatorHelper();
		List<String> errors = new ArrayList<String>();
		
		if(!validator.lengthValid(view.getSuName(), 2, 50)){
			errors.add("O nome deve ter no mínimo 2 caracteres.");
		}
		
		if (!validator.emailValid(view.getSuEmail())){
			errors.add("Email inválido.");
		}

		if (!validator.passwordValid(view.getSuPassword())){
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
		view.hideUserCreatedAlert();
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
							view.showUserCreatedAlert();
							view.displayLoginPanel(true);
						}
					});
				}

				private RegistrationRequestTO buildRegistrationRequestTO() {
					RegistrationRequestTO registrationRequestTO = clientFactory.getTOFactory().newRegistrationRequestTO().as();
					registrationRequestTO.setFullName(view.getSuName().trim());
					registrationRequestTO.setEmail(view.getSuEmail().toLowerCase().trim());
					registrationRequestTO.setPassword(view.getSuPassword().trim());
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
		view.displayLoginPanel(true);		
	}

	@Override
	public void checkIfUserWasCreated() {
		Place currentPlace = clientFactory.getPlaceController().getWhere();
		if (currentPlace instanceof VitrinePlace && ((VitrinePlace) currentPlace).isUserCreated()) {
			view.showUserCreatedAlert();
			((VitrinePlace) currentPlace).setUserCreated(false);
		}
	}

}