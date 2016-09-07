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

import kornell.api.client.Callback;
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
		assetsFields.add(buildFileUploadPanel("logo250x45.png", "image/png", "Logo 250x45 - escura"));
		assetsFields.add(buildFileUploadPanel("logo250x45_light.png", "image/png", "Logo 250x45 - clara"));
		assetsFields.add(buildFileUploadPanel("logo300x80.png", "image/png", "Logo 300x80 - escura"));
		assetsFields.add(buildFileUploadPanel("logo300x80_light.png", "image/png", "Logo 300x80 - clara"));
		assetsFields.add(buildFileUploadPanel("bgVitrine.jpg", "image/jpeg", "Background da Vitrine"));
		assetsFields.add(buildFileUploadPanel("favicon.ico", "image/x-icon", "Favicon"));
	}

	private FlowPanel buildFileUploadPanel(final String fileName, final String contentType, String label) {
		// Create a FormPanel and point it at a service
	    final FormPanel form = new FormPanel();
	    final String elementId = fileName.replace('.', '-');

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
		fileUpload.setId(elementId);
		fileUploadPanel.add(fileUpload);
		fieldPanelWrapper.add(fileUpload);
		
	    // Add a submit button to the form
		Button btnOK = new Button("Atualizar");
		btnOK.addStyleName("btnAction btnStandard");
		btnOK.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				session.institution(institution.getUUID()).getUploadURL(fileName, new Callback<String>() {
					@Override
					public void ok(String url) {
						getFile(elementId, contentType, url);
					}
				});		
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
	
	public static native void getFile(String elementId, String contentType, String url) /*-{
	if ($wnd.document.getElementById(elementId).files.length != 1) {
    	@kornell.gui.client.util.view.KornellNotification::showError(Ljava/lang/String;)("Por favor selecione uma imagem");
	} else {
		@kornell.gui.client.util.view.LoadingPopup::show()();
		var file = $wnd.document.getElementById(elementId).files[0];
		if (file.name.indexOf(elementId.split("-")[1]) == -1) {
        	@kornell.gui.client.util.view.KornellNotification::showError(Ljava/lang/String;)("Faça o upload de uma imagem do formato exigido");
			@kornell.gui.client.util.view.LoadingPopup::hide()();
		} else {
			var req = new XMLHttpRequest();
			req.open('PUT', url);
			req.setRequestHeader("Content-type", contentType);
			req.onreadystatechange = function() {
				if (req.readyState == 4 && req.status == 200) {
    				@kornell.gui.client.util.view.LoadingPopup::hide()();
    				@kornell.gui.client.util.view.KornellNotification::show(Ljava/lang/String;)("Atualização de imagem completa");
				}
			}
			req.send(file);
		}
	}
}-*/;
	
}