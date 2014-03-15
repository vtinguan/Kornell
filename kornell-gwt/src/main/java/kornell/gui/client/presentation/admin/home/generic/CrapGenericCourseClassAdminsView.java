package kornell.gui.client.presentation.admin.home.generic;

import java.util.HashMap;
import java.util.Map;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.People;
import kornell.core.entity.Person;
import kornell.gui.client.presentation.util.FormHelper;

import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.Typeahead;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;

public class CrapGenericCourseClassAdminsView {
	
	private FormHelper formHelper;
	private KornellSession session;
	private TextBox search;
	private MultiWordSuggestOracle oracle;
	private ListBox multipleSelect;
	private Map<String, Person> oraclePeople;
	
	public CrapGenericCourseClassAdminsView(KornellSession session){
		this.session = session;
		formHelper = new FormHelper();
	}

	private void searchChanged(String search) {
		session.people().findBySearchTerm(search, new Callback<People>() {
			@Override
			public void ok(People to) {
				oraclePeople = new HashMap<String, Person>();
				oracle.clear();
				String username, oracleStr;
				int i = 0;
				for (Person person : to.getPeople()) {
					username = person.getEmail() != null ? person.getEmail() : person.getCPF();
					oracleStr = username +
								(person.getFullName() != null && !"".equals(person.getFullName()) ?
								" (" + person.getFullName() + ")" : "");
					oracle.add(oracleStr);
					oraclePeople.put(username, person);
					if(++i == 8) break;
				}
			}
		});		
	}

	public FlowPanel createCourseClassAdminsPanel() {
		FlowPanel fieldPanelWrapper = new FlowPanel();
		fieldPanelWrapper.addStyleName("fieldPanelWrapper courseClassAdminField");
		
		FlowPanel labelPanel = new FlowPanel();
		labelPanel.addStyleName("labelPanel");
		Label lblLabel = new Label("Administradores da Turma");
		lblLabel.addStyleName("lblLabel");
		labelPanel.add(lblLabel);
		fieldPanelWrapper.add(labelPanel);
		
		FlowPanel courseClassAdminsPanel = getCourseClassAdminsFieldPanel();
		fieldPanelWrapper.add(courseClassAdminsPanel);
		
		fieldPanelWrapper.add(formHelper.getImageSeparator());
		
		return fieldPanelWrapper;
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

		multipleSelectPanel.add(multipleSelect);
		
		Button btnRemove = new Button("REMOVER");
		btnRemove.addStyleName("btnNotSelected btnStandard");
		
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
		Typeahead typeahead = new Typeahead();
	    search = new TextBox();
	    search.addStyleName("textField");
	    search.setPlaceholder("Digite o nome ou usuário");
	    
	    search.addKeyUpHandler(new KeyUpHandler() {
	    	String currentSearch = "";
	    	
			Timer searchChangesTimer = new Timer() {
				@Override
				public void run() {
					searchChanged(currentSearch);
				}
			};
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				searchChangesTimer.cancel();
				if(currentSearch.length() >= 1 && !currentSearch.equals(search.getText())){
					searchChangesTimer.schedule(500);
				}
				currentSearch = search.getText();
			}
		});
	    
	    typeahead.add(search);
		
		oracle = (MultiWordSuggestOracle) typeahead.getSuggestOracle();
			
		
		typeaheadPanel.add(typeahead);

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
		String item = search.getText().split(" \\(")[0];
		if(oraclePeople.containsKey(item)){
			if(!formHelper.isItemInListBox(item, multipleSelect)){
				multipleSelect.addItem(item);
			}
			search.setText("");
		}
	}
}
