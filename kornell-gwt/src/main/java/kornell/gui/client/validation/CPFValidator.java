package kornell.gui.client.validation;

import java.util.ArrayList;
import java.util.List;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.util.StringUtils;
import kornell.gui.client.presentation.util.FormHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

public class CPFValidator implements Validator {
	ValidationMessages msgs = GWT.create(ValidationMessages.class);
	KornellSession session;

	public CPFValidator(KornellSession session) {
		this.session = session;
	}

	public static final CPFValidator unregisteredCPFValidator(
			KornellSession session) {
		CPFValidator unregisteredCPFVal = new CPFValidator(session);
		return unregisteredCPFVal;
	}

	@Override
	public void getErrors(Widget widget, final Callback<List<String>> cb) {
		final List<String> errors = new ArrayList<String>();
		if (widget instanceof HasText) {
			HasText txtWidget = (HasText) widget;
			String cpf = txtWidget.getText();
			if (StringUtils.isSome(cpf)) {
				if (!isValidCPF(cpf)) {
					errors.add(msgs.invalidCPF());
					cb.ok(errors);
				} else {
					session.people().isCPFRegistered(cpf, new Callback<Boolean>() {
						@Override
						public void ok(Boolean to) {
							if (to) {
								errors.add(msgs.cpfAlreadyRegistered());
							}
							cb.ok(errors);
						}
					});
				}
			} else {
				cb.ok(errors);
			}
		}
	}

	private static boolean isValidCPF(String cpf) {
		return FormHelper.isCPFValid(cpf);
	}

	
	
	
	

}
