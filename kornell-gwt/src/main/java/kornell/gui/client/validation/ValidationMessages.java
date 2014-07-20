package kornell.gui.client.validation;

import com.google.gwt.i18n.client.Messages;

public interface ValidationMessages extends Messages{
	String invalidCPF();
	String existingCPF();
	String invalidEmail();
	String existingEmail();
}
