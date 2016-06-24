package kornell.gui.client.presentation.admin.courseclass.courseclass.generic;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.CourseClassAdminRole;
import kornell.core.entity.EntityFactory;
import kornell.core.entity.ObserverRole;
import kornell.core.entity.Role;
import kornell.core.entity.RoleCategory;
import kornell.core.entity.RoleType;
import kornell.core.entity.Roles;
import kornell.core.entity.TutorRole;
import kornell.core.to.CourseClassTO;
import kornell.core.to.RoleTO;
import kornell.core.to.RolesTO;
import kornell.gui.client.presentation.admin.courseclass.courseclass.AdminCourseClassView.Presenter;
import kornell.gui.client.util.forms.formfield.PeopleMultipleSelect;
import kornell.gui.client.util.view.KornellNotification;
import kornell.gui.client.util.view.LoadingPopup;

public class GenericCourseClassAdminsView extends Composite {
	interface MyUiBinder extends UiBinder<Widget, GenericCourseClassAdminsView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	public static final EntityFactory entityFactory = GWT.create(EntityFactory.class);

	private KornellSession session;
	boolean isCurrentUser, showContactDetails, isRegisteredWithCPF;
	
	PeopleMultipleSelect courseClassAdminsMultipleSelect;
	PeopleMultipleSelect tutorsMultipleSelect;
	PeopleMultipleSelect observersMultipleSelect;

	@UiField
	Form courseClassAdminsForm;
	@UiField
	FlowPanel courseClassAdminsFields;
	@UiField
	Button courseClassAdminsBtnOK;
	@UiField
	Button courseClassAdminsBtnCancel;
	
	@UiField
    Form tutorsForm;
    @UiField
    FlowPanel tutorsFields;
    @UiField
    Button tutorsBtnOK;
    @UiField
    Button tutorsBtnCancel;
    
    @UiField
    Form observersForm;
    @UiField
    FlowPanel observersFields;
    @UiField
    Button observersBtnOK;
    @UiField
    Button observersBtnCancel;

	private CourseClassTO courseClassTO;
	
	public GenericCourseClassAdminsView(final KornellSession session,
			Presenter presenter, CourseClassTO courseClassTO) {
		this.session = session;
		this.courseClassTO = courseClassTO;
		initWidget(uiBinder.createAndBindUi(this));

		// i18n
		courseClassAdminsBtnOK.setText("Salvar Alterações");
		courseClassAdminsBtnCancel.setText("Cancelar Alterações");
		tutorsBtnOK.setText("Salvar Alterações");
		tutorsBtnCancel.setText("Cancelar Alterações");
        observersBtnOK.setText("Salvar Alterações");
        observersBtnCancel.setText("Cancelar Alterações");

		this.courseClassTO = courseClassTO;
		
		initCourseClassAdminsData();
		initTutorsData();
		initObserversData();
	}

	public void initCourseClassAdminsData() {
	    courseClassAdminsFields.clear();
		FlowPanel fieldPanelWrapper = new FlowPanel();
		fieldPanelWrapper.addStyleName("fieldPanelWrapper");
		
		FlowPanel labelPanel = new FlowPanel();
		labelPanel.addStyleName("labelPanel");
		Label lblLabel = new Label("Administradores da Turma");
		lblLabel.addStyleName("lblLabel");
		labelPanel.add(lblLabel);
		fieldPanelWrapper.add(labelPanel);
		
		LoadingPopup.show();
		session.courseClass(courseClassTO.getCourseClass().getUUID()).getAdmins(RoleCategory.BIND_WITH_PERSON,
				new Callback<RolesTO>() {
			@Override
			public void ok(RolesTO to) {
				for (RoleTO roleTO : to.getRoleTOs()) {
					String item = roleTO.getUsername();
					if(roleTO.getPerson().getFullName() != null && !"".equals(roleTO.getPerson().getFullName())){
						item += " (" +roleTO.getPerson().getFullName()+")";
					}
					courseClassAdminsMultipleSelect.addItem(item, roleTO.getPerson().getUUID());
				}
				LoadingPopup.hide();
			}
		});
		courseClassAdminsMultipleSelect = new PeopleMultipleSelect(session);
		fieldPanelWrapper.add(courseClassAdminsMultipleSelect.asWidget());
		
		courseClassAdminsFields.add(fieldPanelWrapper);
	}
	
	public void initTutorsData() {
        tutorsFields.clear();
        FlowPanel fieldPanelWrapper = new FlowPanel();
        fieldPanelWrapper.addStyleName("fieldPanelWrapper");
        
        FlowPanel labelPanel = new FlowPanel();
        labelPanel.addStyleName("labelPanel");
        Label lblLabel = new Label("Tutores da Turma");
        lblLabel.addStyleName("lblLabel");
        labelPanel.add(lblLabel);
        fieldPanelWrapper.add(labelPanel);
        
        LoadingPopup.show();
        session.courseClass(courseClassTO.getCourseClass().getUUID()).getTutors(RoleCategory.BIND_WITH_PERSON,
                new Callback<RolesTO>() {
            @Override
            public void ok(RolesTO to) {
                for (RoleTO roleTO : to.getRoleTOs()) {
                    String item = roleTO.getUsername();
                    if(roleTO.getPerson().getFullName() != null && !"".equals(roleTO.getPerson().getFullName())){
                        item += " (" +roleTO.getPerson().getFullName()+")";
                    }
                    tutorsMultipleSelect.addItem(item, roleTO.getPerson().getUUID());
                }
                LoadingPopup.hide();
            }
        });
        tutorsMultipleSelect = new PeopleMultipleSelect(session);
        fieldPanelWrapper.add(tutorsMultipleSelect.asWidget());
        
        tutorsFields.add(fieldPanelWrapper);
    }
	
	public void initObserversData() {
        observersFields.clear();
        FlowPanel fieldPanelWrapper = new FlowPanel();
        fieldPanelWrapper.addStyleName("fieldPanelWrapper");
        
        FlowPanel labelPanel = new FlowPanel();
        labelPanel.addStyleName("labelPanel");
        Label lblLabel = new Label("Observadores da Turma");
        lblLabel.addStyleName("lblLabel");
        labelPanel.add(lblLabel);
        fieldPanelWrapper.add(labelPanel);
        
        LoadingPopup.show();
        session.courseClass(courseClassTO.getCourseClass().getUUID()).getObservers(RoleCategory.BIND_WITH_PERSON,
                new Callback<RolesTO>() {
            @Override
            public void ok(RolesTO to) {
                for (RoleTO roleTO : to.getRoleTOs()) {
                    String item = roleTO.getUsername();
                    if(roleTO.getPerson().getFullName() != null && !"".equals(roleTO.getPerson().getFullName())){
                        item += " (" +roleTO.getPerson().getFullName()+")";
                    }
                    observersMultipleSelect.addItem(item, roleTO.getPerson().getUUID());
                }
                LoadingPopup.hide();
            }
        });
        observersMultipleSelect = new PeopleMultipleSelect(session);
        fieldPanelWrapper.add(observersMultipleSelect.asWidget());
        
        observersFields.add(fieldPanelWrapper);
    }

	@UiHandler("courseClassAdminsBtnOK")
	void doOKCourseClassAdmins(ClickEvent e) {
		if(session.isInstitutionAdmin()){
			Roles roles = entityFactory.newRoles().as();
			List<Role> rolesList = new ArrayList<Role>();
			ListBox multipleSelect = courseClassAdminsMultipleSelect.getMultipleSelect();
			for (int i = 0; i < multipleSelect.getItemCount(); i++) {
				String personUUID = multipleSelect.getValue(i);
				Role role = entityFactory.newRole().as();
				CourseClassAdminRole courseClassAdminRole = entityFactory.newCourseClassAdminRole().as();
				role.setPersonUUID(personUUID);
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
	
	@UiHandler("tutorsBtnOK")
    void doOKTutors(ClickEvent e) {
        if(session.isInstitutionAdmin()){
            Roles roles = entityFactory.newRoles().as();
            List<Role> rolesList = new ArrayList<Role>();
            ListBox multipleSelect = tutorsMultipleSelect.getMultipleSelect();
            if(multipleSelect.getItemCount() == 0 && courseClassTO.getCourseClass().isTutorChatEnabled()){
            	KornellNotification.show("Você não pode remover todos os tutores desta turma. Desabilite a opção \"Permitir tutoria da turma\" na aba Configurações.", AlertType.WARNING, 4000);
            	return;
            }
            for (int i = 0; i < multipleSelect.getItemCount(); i++) {
                String personUUID = multipleSelect.getValue(i);
                Role role = entityFactory.newRole().as();
                TutorRole tutorRole = entityFactory.newTutorRole().as();
                role.setPersonUUID(personUUID);
                role.setRoleType(RoleType.tutor);
                tutorRole.setCourseClassUUID(courseClassTO.getCourseClass().getUUID());
                role.setTutorRole(tutorRole);
                rolesList.add(role);  
            }
			roles.setRoles(rolesList);
            session.courseClass(courseClassTO.getCourseClass().getUUID()).updateTutors(roles, new Callback<Roles>() {
                @Override
                public void ok(Roles to) {
                    KornellNotification.show("Os tutores da turma foram atualizados com sucesso.", AlertType.SUCCESS);
                }
            });
        }
    }
	
	@UiHandler("observersBtnOK")
    void doOKObservers(ClickEvent e) {
        if(session.isInstitutionAdmin()){
            Roles roles = entityFactory.newRoles().as();
            List<Role> rolesList = new ArrayList<Role>();
            ListBox multipleSelect = observersMultipleSelect.getMultipleSelect();
            for (int i = 0; i < multipleSelect.getItemCount(); i++) {
                String personUUID = multipleSelect.getValue(i);
                Role role = entityFactory.newRole().as();
                ObserverRole observerRole = entityFactory.newObserverRole().as();
                role.setPersonUUID(personUUID);
                role.setRoleType(RoleType.observer);
                observerRole.setCourseClassUUID(courseClassTO.getCourseClass().getUUID());
                role.setObserverRole(observerRole);
                rolesList.add(role);  
            }
			roles.setRoles(rolesList);
            session.courseClass(courseClassTO.getCourseClass().getUUID()).updateObservers(roles, new Callback<Roles>() {
                @Override
                public void ok(Roles to) {
                    KornellNotification.show("Os observadores da turma foram atualizados com sucesso.", AlertType.SUCCESS);
                }
            });
        }
    }

	@UiHandler("courseClassAdminsBtnCancel")
	void doCancelCourseClassAdmins(ClickEvent e) {
		initCourseClassAdminsData();
	}
	
	@UiHandler("tutorsBtnCancel")
    void doCancelTutors(ClickEvent e) {
        initTutorsData();
    }
	
	@UiHandler("observersBtnCancel")
    void doCancelObservers(ClickEvent e) {
        initObserversData();
    }

}