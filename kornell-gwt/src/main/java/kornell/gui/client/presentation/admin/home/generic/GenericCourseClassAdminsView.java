package kornell.gui.client.presentation.admin.home.generic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.CourseClassAdminRole;
import kornell.core.entity.EntityFactory;
import kornell.core.entity.People;
import kornell.core.entity.Person;
import kornell.core.entity.Role;
import kornell.core.entity.RoleCategory;
import kornell.core.entity.RoleType;
import kornell.core.entity.Roles;
import kornell.core.to.CourseClassTO;
import kornell.core.to.RoleTO;
import kornell.core.to.RolesTO;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.presentation.admin.home.AdminHomeView.Presenter;
import kornell.gui.client.presentation.util.FormHelper;
import kornell.gui.client.presentation.util.KornellNotification;
import kornell.gui.client.util.view.formfield.KornellFormFieldWrapper;

import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.Typeahead;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.Widget;

public class GenericCourseClassAdminsView extends Composite {
	interface MyUiBinder extends UiBinder<Widget, GenericCourseClassAdminsView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	public static final EntityFactory entityFactory = GWT.create(EntityFactory.class);
	private KornellConstants constants = GWT.create(KornellConstants.class);

	private KornellSession session;
	private FormHelper formHelper;
	boolean isCurrentUser, showContactDetails, isRegisteredWithCPF;
	
	private TextBox search;
	private MultiWordSuggestOracle oracle;
	private ListBox multipleSelect;
	private Map<String, Person> oraclePeople;


	@UiField
	Form form;
	@UiField
	FlowPanel adminsFields;
	@UiField
	Button btnOK;
	@UiField
	Button btnCancel;

	private UserInfoTO user;
	private CourseClassTO courseClassTO;
	private List<KornellFormFieldWrapper> fields;
	
	public GenericCourseClassAdminsView(final KornellSession session,
			Presenter presenter, CourseClassTO courseClassTO) {
		this.session = session;
		this.user = session.getCurrentUser();
		this.courseClassTO = courseClassTO;
		formHelper = new FormHelper();
		initWidget(uiBinder.createAndBindUi(this));

		// i18n
		btnOK.setText("OK".toUpperCase());
		btnCancel.setText("Limpar".toUpperCase());

		this.courseClassTO = courseClassTO;
		
		initData();
	}

	public void initData() {
		adminsFields.clear();
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
		
		adminsFields.add(fieldPanelWrapper);
	}

	private void searchChanged(String search) {
		session.people().findBySearchTerm(search, Dean.getInstance().getInstitution().getUUID(), new Callback<People>() {
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
					if(++i == 10) break;
				}
			}
		});		
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
		
		session.courseClass(courseClassTO.getCourseClass().getUUID()).getAdmins(RoleCategory.BIND_WITH_PERSON,
				new Callback<RolesTO>() {
			
			@Override
			public void ok(RolesTO to) {
				for (RoleTO roleTO : to.getRoleTOs()) {
					String item = roleTO.getRole().getPersonUUID();
					if(roleTO.getPerson().getFullName() != null && !"".equals(roleTO.getPerson().getFullName())){
						item += " (" +roleTO.getPerson().getFullName()+")";
					}
					multipleSelect.addItem(item);
				}
			}
		});
				
		return multipleSelectPanel;
	}

	private FlowPanel getTypeAheadPanel() {
		FlowPanel typeaheadPanel = new FlowPanel();
		Typeahead typeahead = new Typeahead();
	    search = new TextBox();
	    search.addStyleName("textField");
	    search.setPlaceholder("Digite o nome de usuário a adicionar");
	    
	    search.addKeyUpHandler(new KeyUpHandler() {
	    	String currentSearch = "";
	    	String previousSearch = "";
	    	
			Timer searchChangesTimer = new Timer() {
				@Override
				public void run() {
					searchChanged(currentSearch);
				}
			};
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				currentSearch = search.getText();
				if(currentSearch.length() >= 1 && !currentSearch.equals(previousSearch)){
					searchChangesTimer.cancel();
					searchChangesTimer.schedule(100);
				}
				previousSearch = currentSearch;
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
		if(oraclePeople != null && oraclePeople.containsKey(item)){
			if(!formHelper.isItemInListBox(item, multipleSelect)){
				multipleSelect.addItem(item);
			}
			search.setText("");
		}
	}

	@UiHandler("btnOK")
	void doOK(ClickEvent e) {
		if(session.isInstitutionAdmin()){
			Roles roles = entityFactory.newRoles().as();
			List<Role> rolesList = new ArrayList<Role>();
			for (int i = 0; i < multipleSelect.getItemCount(); i++) {
				String username = multipleSelect.getItemText(i);
				Role role = entityFactory.newRole().as();
				CourseClassAdminRole courseClassAdminRole = entityFactory.newCourseClassAdminRole().as();
				role.setPersonUUID(username);
				role.setRoleType(RoleType.courseClassAdmin);
				courseClassAdminRole.setCourseClassUUID(courseClassTO.getCourseClass().getUUID());
				role.setCourseClassAdminRole(courseClassAdminRole);
				rolesList.add(role);  
			}
			roles.setRoles(rolesList);
			session.courseClass(courseClassTO.getCourseClass().getUUID()).updateAdmins(roles, new Callback<Roles>() {
				@Override
				public void ok(Roles to) {
					KornellNotification.show("Os administradores da turma foram atualizados com sucesso.", AlertType.SUCCESS);
				}
			});
		}
		
	}

	@UiHandler("btnCancel")
	void doCancel(ClickEvent e) {
		initData();
	}

}