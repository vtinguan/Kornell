package kornell.gui.client.presentation.profile.generic;

import java.util.ArrayList;
import java.util.List;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.presentation.profile.ProfileView;
import kornell.gui.client.presentation.util.FormHelper;
import kornell.gui.client.presentation.util.KornellNotification;
import kornell.gui.client.presentation.util.LoadingPopup;
import kornell.gui.client.util.view.formfield.KornellFormFieldWrapper;

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

public class GenericPasswordChangeView extends Composite implements ProfileView {
	interface MyUiBinder extends UiBinder<Widget, GenericPasswordChangeView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

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

		modalNewPassword = new KornellFormFieldWrapper("Nova Senha", formHelper.createPasswordTextBoxFormField(""), true);
		fields.add(modalNewPassword);
		passwordChangeFields.add(modalNewPassword);

		modalNewPasswordConfirm = new KornellFormFieldWrapper("Confirmar Senha", formHelper.createPasswordTextBoxFormField(""), true);
		fields.add(modalNewPasswordConfirm);
		passwordChangeFields.add(modalNewPasswordConfirm);

		passwordChangeFields.add(formHelper.getImageSeparator());
	}

	private boolean validateFields() {
		if (!formHelper.isPasswordValid(modalNewPassword.getFieldPersistText())) {
			modalNewPassword.setError("Senha inválida (mínimo de 6 caracteres).");
		} else if(modalNewPassword.getFieldPersistText().indexOf(':') >= 0){
			modalNewPassword.setError("Senha inválida (não pode conter o caractere ':').");
		}
		if (!modalNewPassword.getFieldPersistText().equals(modalNewPasswordConfirm.getFieldPersistText())) {
			modalNewPasswordConfirm.setError("As senhas não conferem.");
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
					KornellNotification.show("Senha alterada com sucesso!");
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