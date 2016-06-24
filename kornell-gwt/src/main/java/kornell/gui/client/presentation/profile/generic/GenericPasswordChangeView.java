package kornell.gui.client.presentation.profile.generic;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Modal;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.presentation.profile.ProfileView;
import kornell.gui.client.util.forms.FormHelper;
import kornell.gui.client.util.forms.formfield.KornellFormFieldWrapper;
import kornell.gui.client.util.view.KornellNotification;
import kornell.gui.client.util.view.LoadingPopup;

public class GenericPasswordChangeView extends Composite implements ProfileView {
	interface MyUiBinder extends UiBinder<Widget, GenericPasswordChangeView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private static KornellConstants constants = GWT.create(KornellConstants.class);

	private KornellSession session;
	private FormHelper formHelper;

	@UiField
	Modal passwordChangeModal;
	@UiField
	FlowPanel passwordChangeFields;
	@UiField
	Button btnOK;
	@UiField
	Button btnCancel;

	private UserInfoTO user;
	private KornellFormFieldWrapper modalNewPassword, modalNewPasswordConfirm;
	private List<KornellFormFieldWrapper> fields;
	private boolean initialized;


	public GenericPasswordChangeView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void show(){
		if(initialized)
			passwordChangeModal.show();
	}

	public void initData(KornellSession session, UserInfoTO user) {
		this.session = session;
		this.user = user;
		formHelper = new FormHelper();
		fields = new ArrayList<KornellFormFieldWrapper>();
		passwordChangeFields.clear();
		initialized = true;

		btnOK.setText("OK".toUpperCase());
		btnCancel.setText("Cancelar".toUpperCase());

		modalNewPassword = new KornellFormFieldWrapper(constants.newPassword(), formHelper.createPasswordTextBoxFormField(""), true);
		fields.add(modalNewPassword);
		passwordChangeFields.add(modalNewPassword);

		modalNewPasswordConfirm = new KornellFormFieldWrapper(constants.confirmPassword(), formHelper.createPasswordTextBoxFormField(""), true);
		fields.add(modalNewPasswordConfirm);
		passwordChangeFields.add(modalNewPasswordConfirm);

		passwordChangeFields.add(formHelper.getImageSeparator());
		
		passwordChangeModal.setTitle(constants.changePasswordButton());
	}

	private boolean validateFields() {
		if (!formHelper.isPasswordValid(modalNewPassword.getFieldPersistText())) {
			modalNewPassword.setError(constants.invalidPasswordTooShort());
		} else if(modalNewPassword.getFieldPersistText().indexOf(':') >= 0){
			modalNewPassword.setError(constants.invalidPasswordBadChar());
		}
		if (!modalNewPassword.getFieldPersistText().equals(modalNewPasswordConfirm.getFieldPersistText())) {
			modalNewPasswordConfirm.setError(constants.passwordMismatch());
		}
		return !checkErrors();
	}

	@UiHandler("btnOK")
	void doOK(ClickEvent e) { 
		formHelper.clearErrors(fields);

		if(validateFields()){
			LoadingPopup.show();
			session.user().changeTargetPassword(user.getPerson().getUUID(), modalNewPassword.getFieldPersistText(), new Callback<Void>() {
				@Override
				public void ok(Void to) {
					LoadingPopup.hide();
					passwordChangeModal.hide();
					KornellNotification.show(constants.confirmPasswordChange());
				}
			});
		}
	}

	@UiHandler("btnCancel")
	void doCancel(ClickEvent e) {
		passwordChangeModal.hide();
	}

	private boolean checkErrors() {
		for (KornellFormFieldWrapper field : fields) 
			if(!"".equals(field.getError()))
				return true;		
		return false;
	}

	@Override
	public void setPresenter(Presenter presenter) {
		// TODO Auto-generated method stub
	}

}