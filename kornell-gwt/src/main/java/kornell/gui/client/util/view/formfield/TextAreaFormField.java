package kornell.gui.client.util.view.formfield;

import kornell.gui.client.presentation.util.FormHelper;

import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;

public class TextAreaFormField implements KornellFormField<TextBox> {

	private final FormHelper formHelper = GWT.create(FormHelper.class);
	
	public static final String CPF = "cpf";
	
	TextArea field;
	
	public TextAreaFormField(TextArea field) {
		this.field = field;
		field.addStyleName("field");
		field.addStyleName("textField");
		
	}

	@Override
	public Widget getFieldWidget() {
		return field;
	}

	@Override
	public String getDisplayText() {
			return field.getValue();
	}

	@Override
	public String getPersistText() {
		return field.getValue();
	}

}
