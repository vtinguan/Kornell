package kornell.gui.client.uidget.formfield;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Widget;

public class CheckBoxFormField implements KornellFormField<CheckBox> {

	CheckBox field;
	
	public CheckBoxFormField(CheckBox field) {
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
		//TODO i18n
		return field.getValue() ? "Sim" : "Não";
	}

	@Override
	public String getPersistText() {
		return field.getValue().toString();
	}

}
