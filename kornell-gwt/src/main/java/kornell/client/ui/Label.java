package kornell.client.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.user.client.ui.Widget;

public class Label extends Widget {
	LabelElement el = Document.get().createLabelElement();
	public Label() {
		setElement(el);
	}
	
	public void setText(String text){
		el.setInnerText(text);
	}

}
