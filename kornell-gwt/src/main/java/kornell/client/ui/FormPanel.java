package kornell.client.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.FieldSetElement;
import com.google.gwt.dom.client.FormElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.LegendElement;
import com.google.gwt.dom.client.OListElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;

public class FormPanel extends ComplexPanel {
	final FormElement form = Document.get().createFormElement();
	final FieldSetElement fieldset = Document.get().createFieldSetElement();
	final LegendElement legend = Document.get().createLegendElement();
	final OListElement ol = Document.get().createOLElement();

	public FormPanel() {
		form.setClassName("cmxform");
		fieldset.appendChild(legend);
		fieldset.appendChild(ol);		
		form.appendChild(fieldset);
	    setElement(form);
	}

	public void add(Widget w) {
	    LIElement li = Document.get().createLIElement();
	    ol.appendChild(li);
	    add(w, (Element)li.cast());
	}

	public void insert(Widget w, int beforeIndex) {
	    checkIndexBoundsForInsertion(beforeIndex);

	    LIElement li = Document.get().createLIElement();
	    ol.insertBefore(li, ol.getChild(beforeIndex));
	    insert(w, (Element)li.cast(), beforeIndex, false);
	}

	public boolean remove(Widget w) {
	    Element li = DOM.getParent(w.getElement());
	    boolean removed = super.remove(w);
	    if (removed) {
	        ol.removeChild(li);
	    }
	    return removed;
	}
	
	public void setLegend(String text){
		legend.setInnerText(text);
	}

}
