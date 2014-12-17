package kornell.gui.client.util.view.formfield;

import java.util.HashMap;
import java.util.Map;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.People;
import kornell.core.entity.Person;
import kornell.core.entity.RoleCategory;
import kornell.core.to.RoleTO;
import kornell.core.to.RolesTO;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.presentation.util.FormHelper;

import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.Typeahead;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.Widget;

public class SimpleMultipleSelect extends Composite {
	private FormHelper formHelper = GWT.create(FormHelper.class);
	
	private KornellSession session;
	private TextBox search;
	private MultiWordSuggestOracle oracle;
	private ListBox multipleSelect;

	public SimpleMultipleSelect() {
	}
	
	@Override
	public Widget asWidget(){
		return getCourseClassAdminsFieldPanel();
	}

	private FlowPanel getCourseClassAdminsFieldPanel() {
		FlowPanel courseClassAdminsPanel = new FlowPanel();
		courseClassAdminsPanel.addStyleName("fieldPanel");
		
		courseClassAdminsPanel.add(getTypeAheadPanel());
		FlowPanel multipleSelectPanel = getMultipleSelectPanel();
		courseClassAdminsPanel.add(multipleSelectPanel);
		
		return courseClassAdminsPanel;
	}


	private FlowPanel getMultipleSelectPanel() {
		FlowPanel multipleSelectPanel = new FlowPanel();
		multipleSelect = new ListBox(true);
		multipleSelect.addStyleName("selectField");
		multipleSelect.addStyleName("height200");

		multipleSelectPanel.add(multipleSelect);
		
		Button btnRemove = new Button("REMOVER");
		btnRemove.addStyleName("btnSelected btnStandard");
		
		btnRemove.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				while(multipleSelect.getSelectedIndex() != -1){
					multipleSelect.removeItem(multipleSelect.getSelectedIndex());
				}
			}
		});
		multipleSelectPanel.add(btnRemove);
				
		return multipleSelectPanel;
	}

	private FlowPanel getTypeAheadPanel() {
		FlowPanel typeaheadPanel = new FlowPanel();
    search = new TextBox();
    search.addStyleName("textField");
    search.setPlaceholder("Digite o nome do domínio a adicionar");
		typeaheadPanel.add(search);

		Button btnAdd = new Button("ADICIONAR");
		btnAdd.addStyleName("btnAction btnStandard");
		
		btnAdd.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addAdmin();
			}
		});
		
		typeaheadPanel.add(btnAdd);
		
		return typeaheadPanel;
	}

	private void addAdmin() {
		boolean found = false;
		for (int i = 0; i < multipleSelect.getItemCount(); i++) {
	    if(multipleSelect.getValue(i).equals(search.getText())){
	    	found = true;
	    	break;
	    }
    }
		if(!found)
			multipleSelect.addItem(search.getText(), search.getText());
		
		search.setText("");
	}

	public ListBox getMultipleSelect() {
	  return multipleSelect;
  }

	public void addItem(String item, String uuid) {
		multipleSelect.addItem(item, uuid);
  }
}