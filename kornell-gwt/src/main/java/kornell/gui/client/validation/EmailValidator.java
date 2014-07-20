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

public class EmailValidator implements Validator {
	ValidationMessages msgs = GWT.create(ValidationMessages.class);
	KornellSession session;

	public EmailValidator(KornellSession session) {
		this.session = session;
	}

	public static final EmailValidator unregisteredEmailValidator(
			KornellSession session) {
		EmailValidator validator = new EmailValidator(session);
		return validator;
	}

	@Override
	public void getErrors(Widget widget, final Callback<List<String>> cb) {
		final List<String> errors = new ArrayList<String>();
		if (widget instanceof HasText) {
			HasText txtWidget = (HasText) widget;
			String email = txtWidget.getText();
			if (StringUtils.isSome(email)) {
				if (!isValidEmail(email)) {
					errors.add(msgs.invalidEmail());
					cb.ok(errors);
				} else {
					session.people().isEmailRegistered(email, new Callback<Boolean>() {
						@Override
						public void ok(Boolean exists) {
							if (exists) {
								errors.add(msgs.existingEmail());
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

	private static boolean isValidEmail(String email) {
		boolean emailValid = FormHelper.isEmailValid(email);
		return emailValid;
	}

	
	
	
	

}
