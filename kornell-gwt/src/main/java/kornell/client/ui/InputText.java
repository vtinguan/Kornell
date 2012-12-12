package kornell.client.ui;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.TextBox;

public class InputText extends Composite implements HasValue<String> {
	TextBox textBox = new TextBox();
	Label label = new Label();
	HTMLPanel span = new HTMLPanel("span","");
	
	public InputText() {
		span.add(label);
		span.add(textBox);
		initWidget(span);
	}
	
	public void setLabel(String text){
		label.setText(text);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<String> handler) {
		return textBox.addValueChangeHandler(handler);
	}

	@Override
	public String getValue() {
		return textBox.getValue();
	}

	@Override
	public void setValue(String value) {
		textBox.setValue(value);
	}

	@Override
	public void setValue(String value, boolean fireEvents) {
		// TODO Auto-generated method stub
		
	}

}
