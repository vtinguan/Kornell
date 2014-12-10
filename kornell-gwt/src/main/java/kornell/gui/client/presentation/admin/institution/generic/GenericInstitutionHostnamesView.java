package kornell.gui.client.presentation.admin.institution.generic;

import java.util.ArrayList;
import java.util.List;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.EntityFactory;
import kornell.core.entity.Institution;
import kornell.core.entity.InstitutionAdminRole;
import kornell.core.entity.Role;
import kornell.core.entity.RoleType;
import kornell.core.entity.Roles;
import kornell.core.to.InstitutionHostNamesTO;
import kornell.core.to.TOFactory;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.presentation.util.FormHelper;
import kornell.gui.client.presentation.util.KornellNotification;
import kornell.gui.client.presentation.util.LoadingPopup;
import kornell.gui.client.util.view.formfield.KornellFormFieldWrapper;
import kornell.gui.client.util.view.formfield.PeopleMultipleSelect;
import kornell.gui.client.util.view.formfield.SimpleMultipleSelect;

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

public class GenericInstitutionHostnamesView extends Composite {
	interface MyUiBinder extends UiBinder<Widget, GenericInstitutionHostnamesView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	public static final TOFactory toFactory = GWT.create(TOFactory.class);
	private KornellConstants constants = GWT.create(KornellConstants.class);
	private FormHelper formHelper = GWT.create(FormHelper.class);

	private KornellSession session;
	boolean isCurrentUser, showContactDetails, isRegisteredWithCPF;
	
	SimpleMultipleSelect simpleMultipleSelect;

	@UiField
	Form form;
	@UiField
	FlowPanel adminsFields;
	@UiField
	Button btnOK;
	@UiField
	Button btnCancel;

	private UserInfoTO user;
	private Institution institution;
	private List<KornellFormFieldWrapper> fields;
	
	public GenericInstitutionHostnamesView(final KornellSession session,
			kornell.gui.client.presentation.admin.institution.AdminInstitutionView.Presenter presenter, Institution institution) {
		this.session = session;
		this.user = session.getCurrentUser();
		this.institution = institution;
		formHelper = new FormHelper();
		initWidget(uiBinder.createAndBindUi(this));

		// i18n
		btnOK.setText("OK".toUpperCase());
		btnCancel.setText("Limpar".toUpperCase());
		
		initData();
	}

	public void initData() {
		adminsFields.clear();
		FlowPanel fieldPanelWrapper = new FlowPanel();
		fieldPanelWrapper.addStyleName("fieldPanelWrapper courseClassAdminField");
		
		FlowPanel labelPanel = new FlowPanel();
		labelPanel.addStyleName("labelPanel");
		Label lblLabel = new Label("Domínios da Instituição");
		lblLabel.addStyleName("lblLabel");
		labelPanel.add(lblLabel);
		fieldPanelWrapper.add(labelPanel);
		

		LoadingPopup.show();
		session.institution(institution.getUUID()).getHostnames(new Callback<InstitutionHostNamesTO>() {
			@Override
			public void ok(InstitutionHostNamesTO to) {
				for (String institutionHostName : to.getInstitutionHostNames()) {
					simpleMultipleSelect.addItem(institutionHostName, institutionHostName);
				}
				LoadingPopup.hide();
			}
		});
		simpleMultipleSelect = new SimpleMultipleSelect();
		fieldPanelWrapper.add(simpleMultipleSelect.asWidget());
		
		
		fieldPanelWrapper.add(formHelper.getImageSeparator());
		adminsFields.add(fieldPanelWrapper);
	}

	@UiHandler("btnOK")
	void doOK(ClickEvent e) {
		if(session.isPlatformAdmin()){
			InstitutionHostNamesTO institutionHostNamesTO = toFactory.newInstitutionHostNamesTO().as();
			List<String> institutionHostNames = new ArrayList<String>();
			ListBox multipleSelect = simpleMultipleSelect.getMultipleSelect();
			for (int i = 0; i < multipleSelect.getItemCount(); i++) {
				institutionHostNames.add(multipleSelect.getValue(i));  
			}
			institutionHostNamesTO.setInstitutionHostNames(institutionHostNames);
			session.institution(institution.getUUID()).updateHostnames(institutionHostNamesTO, new Callback<InstitutionHostNamesTO>() {
				@Override
				public void ok(InstitutionHostNamesTO to) {
					KornellNotification.show("Os domínios da instituição foram atualizados com sucesso.", AlertType.SUCCESS);
				}
			});
		}
		
	}

	@UiHandler("btnCancel")
	void doCancel(ClickEvent e) {
		initData();
	}

}