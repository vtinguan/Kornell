package kornell.gui.client.presentation.admin.institution.generic;

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
import kornell.core.entity.Institution;
import kornell.core.to.InstitutionEmailWhitelistTO;
import kornell.core.to.TOFactory;
import kornell.gui.client.util.forms.formfield.SimpleMultipleSelect;
import kornell.gui.client.util.view.KornellNotification;
import kornell.gui.client.util.view.LoadingPopup;

public class GenericInstitutionEmailWhitelistView extends Composite {
	interface MyUiBinder extends UiBinder<Widget, GenericInstitutionEmailWhitelistView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	public static final TOFactory toFactory = GWT.create(TOFactory.class);

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

	private Institution institution;
	
	public GenericInstitutionEmailWhitelistView(final KornellSession session,
			kornell.gui.client.presentation.admin.institution.AdminInstitutionView.Presenter presenter, Institution institution) {
		this.session = session;
		this.institution = institution;
		initWidget(uiBinder.createAndBindUi(this));

		// i18n
		btnOK.setText("Salvar Alterações");
		btnCancel.setText("Cancelar Alterações");
		
		initData();
	}

	public void initData() {
		adminsFields.clear();
		FlowPanel fieldPanelWrapper = new FlowPanel();
		fieldPanelWrapper.addStyleName("fieldPanelWrapper");
		
		FlowPanel labelPanel = new FlowPanel();
		labelPanel.addStyleName("labelPanel");
		Label lblLabel = new Label("Domínios (sem '@')");
		lblLabel.addStyleName("lblLabel");
		labelPanel.add(lblLabel);
		fieldPanelWrapper.add(labelPanel);
		

		LoadingPopup.show();
		session.institution(institution.getUUID()).getEmailWhitelist(new Callback<InstitutionEmailWhitelistTO>() {
			@Override
			public void ok(InstitutionEmailWhitelistTO to) {
				for (String domain : to.getDomains()) {
					simpleMultipleSelect.addItem(domain, domain);
				}
				LoadingPopup.hide();
			}
		});
		simpleMultipleSelect = new SimpleMultipleSelect();
		fieldPanelWrapper.add(simpleMultipleSelect.asWidget());
		
		adminsFields.add(fieldPanelWrapper);
	}

	@UiHandler("btnOK")
	void doOK(ClickEvent e) {
		if(session.isPlatformAdmin()){
			InstitutionEmailWhitelistTO institutionEmailWhitelistTO = toFactory.newInstitutionEmailWhitelistTO().as();
			List<String> domains = new ArrayList<String>();
			ListBox multipleSelect = simpleMultipleSelect.getMultipleSelect();
			for (int i = 0; i < multipleSelect.getItemCount(); i++) {
				domains.add(multipleSelect.getValue(i));  
			}
			institutionEmailWhitelistTO.setDomains(domains);
			session.institution(institution.getUUID()).updateEmailWhitelist(institutionEmailWhitelistTO, new Callback<InstitutionEmailWhitelistTO>() {
				@Override
				public void ok(InstitutionEmailWhitelistTO to) {
					KornellNotification.show("Os domínios de email permitidos da instituição foram atualizados com sucesso.", AlertType.SUCCESS);
				}
			});
		}
		
	}

	@UiHandler("btnCancel")
	void doCancel(ClickEvent e) {
		initData();
	}

}