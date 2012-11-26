package com.craftware.kornell.client.ui;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;

public class InputText extends Composite {
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

}
