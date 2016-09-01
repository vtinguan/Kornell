package kornell.gui.client.presentation.admin.institution.generic;

import com.github.gwtbootstrap.client.ui.FileUpload;
import com.github.gwtbootstrap.client.ui.Form;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import kornell.api.client.KornellSession;
import kornell.core.entity.EntityFactory;
import kornell.core.entity.Institution;
import kornell.core.util.StringUtils;
import kornell.gui.client.util.forms.formfield.PeopleMultipleSelect;

public class GenericInstitutionAssetsView extends Composite {
	interface MyUiBinder extends UiBinder<Widget, GenericInstitutionAssetsView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	public static final EntityFactory entityFactory = GWT.create(EntityFactory.class);

	private KornellSession session;
	boolean isCurrentUser, showContactDetails, isRegisteredWithCPF;
	
	PeopleMultipleSelect peopleMultipleSelect;

	@UiField
	Form form;
	@UiField
	FlowPanel assetsFields;
	@UiField
	Button btnOK;
	@UiField
	Button btnCancel;

	private Institution institution;
	
	public GenericInstitutionAssetsView(final KornellSession session,
			kornell.gui.client.presentation.admin.institution.AdminInstitutionView.Presenter presenter, Institution institution) {
		this.session = session;
		this.institution = institution;
		initWidget(uiBinder.createAndBindUi(this));

		// i18n
		btnOK.setText("Atualizar");
		btnCancel.setText("Cancelar");
		
		initData();
	}

	public void initData() {
		assetsFields.clear();
		assetsFields.add(buildFileUploadPanel("logo250x45.png", "Logo 250x45 - escura"));
		assetsFields.add(buildFileUploadPanel("logo250x45_light.png", "Logo 250x45 - clara"));
		assetsFields.add(buildFileUploadPanel("logo300x80.png", "Logo 300x80 - escura"));
		assetsFields.add(buildFileUploadPanel("logo300x80_light.png", "Logo 300x80 - clara"));
		assetsFields.add(buildFileUploadPanel("bgVitrine.jpg", "Background da Vitrine"));
		assetsFields.add(buildFileUploadPanel("favicon.ico", "Favicon"));
	}

	private FlowPanel buildFileUploadPanel(String fileName, String label) {
		// Create a FormPanel and point it at a service
	    final FormPanel form = new FormPanel();
	    form.setAction("/institution/"+institution.getUUID()+"/asset/"+fileName);

	    // Because we're going to add a FileUpload widget, we'll need to set the
	    // form to use the POST method, and multipart MIME encoding
	    form.setEncoding(FormPanel.ENCODING_MULTIPART);
	    form.setMethod(FormPanel.METHOD_POST);

	    // Create a panel to hold all of the form widgets
		FlowPanel fieldPanelWrapper = new FlowPanel();
		fieldPanelWrapper.addStyleName("fieldPanelWrapper fileUploadPanel");
	    form.setWidget(fieldPanelWrapper);
		
	    // Create the label panel
		FlowPanel labelPanel = new FlowPanel();
		labelPanel.addStyleName("labelPanel");
		Label lblLabel = new Label(label);
		lblLabel.addStyleName("lblLabel");
		labelPanel.add(lblLabel);
		fieldPanelWrapper.add(labelPanel);

		// Create the FileUpload component
		FlowPanel fileUploadPanel = new FlowPanel();
		FileUpload fileUpload = new FileUpload();
		fileUpload.setName("uploadFormElement");
		fileUploadPanel.add(fileUpload);
		fieldPanelWrapper.add(fileUpload);
		
	    // Add a submit button to the form
		Button btnOK = new Button("Atualizar");
		btnOK.addStyleName("btnAction btnStandard");
		btnOK.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				form.submit();
			}
		});
		fieldPanelWrapper.add(btnOK);
		
		Anchor anchor = new Anchor();
		anchor.setHTML("<icon class=\"fa fa-eye\"></i>");
		anchor.setTitle("Visualizar");
		anchor.setHref(StringUtils.mkurl(institution.getBaseURL(), "repository", institution.getAssetsRepositoryUUID(), fileName));
		anchor.setTarget("_blank");
		fieldPanelWrapper.add(anchor);
		
	    // Add an event handler to the form
	    form.addSubmitHandler(new FormPanel.SubmitHandler() {
			@Override
			public void onSubmit(SubmitEvent event) {
		        // This event is fired just before the form is submitted. We can take
		        // this opportunity to perform validation.
		        //event.cancel();
			}
		});
	    form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
			
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
		        // When the form submission is successfully completed, this event is
		        // fired. Assuming the service returned a response of type text/html,
		        // we can get the result text here (see the FormPanel documentation for
		        // further explanation).
		        Window.alert(event.getResults());
				
			}
		});
	    
		return fieldPanelWrapper;
	}

}