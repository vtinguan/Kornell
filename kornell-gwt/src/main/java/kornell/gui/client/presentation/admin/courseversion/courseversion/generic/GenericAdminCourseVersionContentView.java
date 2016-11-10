package kornell.gui.client.presentation.admin.courseversion.courseversion.generic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.github.gwtbootstrap.client.ui.FileUpload;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dev.util.Name;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.ContentSpec;
import kornell.core.entity.CourseVersion;
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionContentView;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.Wizard;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardElement;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlide;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlideItem;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardTopic;
import kornell.gui.client.presentation.admin.courseversion.courseversion.wizard.WizardMock;
import kornell.gui.client.presentation.admin.courseversion.courseversion.wizard.WizardUtils;
import kornell.gui.client.presentation.admin.courseversion.courseversion.wizard.edit.WizardView;
import kornell.gui.client.util.forms.FormHelper;
import kornell.gui.client.util.forms.formfield.KornellFormFieldWrapper;
import kornell.gui.client.util.view.KornellNotification;
import kornell.gui.client.util.view.LoadingPopup;

public class GenericAdminCourseVersionContentView extends Composite implements AdminCourseVersionContentView {
	interface MyUiBinder extends UiBinder<Widget, GenericAdminCourseVersionContentView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private EventBus bus;
	private KornellSession session;
	boolean isCurrentUser, showContactDetails, isRegisteredWithCPF;
	private FormHelper formHelper = GWT.create(FormHelper.class);
	private List<Label> sideItems;
	private HashMap<String, Label> sidePanelItemsMap;
	private static String HIGHLIGHT_CLASS = "highlightText";
	private String PLAIN_CLASS = "plainDiscreteTextColor";
	private KornellFormFieldWrapper name;
	private List<KornellFormFieldWrapper> fields;

	@UiField
	FlowPanel courseVersionUpload;
	@UiField
	FlowPanel wizardContainer;


	private Wizard wizard;
	private WizardElement selectedWizardElement;
	private CourseVersion courseVersion;
	private Presenter presenter;
	
	private boolean isWizardVersion = false;

	private String nameLabel;
	private String changedString = "(*) ";

	private WizardView wizardView;

	public GenericAdminCourseVersionContentView(final KornellSession session, EventBus bus, PlaceController placeCtrl) {
		this.session = session;
		this.bus = bus;
		initWidget(uiBinder.createAndBindUi(this));

		courseVersionUpload.addStyleName("fieldPanelWrapper fileUploadPanel");
		FlowPanel labelPanel = new FlowPanel();
		labelPanel.addStyleName("labelPanel");
		Label lblLabel = new Label("Atualização de versão");
		lblLabel.addStyleName("lblLabel");
		labelPanel.add(lblLabel);
		courseVersionUpload.add(labelPanel);

		// Create the FileUpload component
		FlowPanel fileUploadPanel = new FlowPanel();
		FileUpload fileUpload = new FileUpload();
		fileUpload.setName("uploadFormElement");
		fileUpload.setId("versionUpdate");
		fileUploadPanel.add(fileUpload);
		courseVersionUpload.add(fileUpload);
		
	    // Add a submit button to the form
		Button btnOK = new Button("Atualizar");
		btnOK.addStyleName("btnAction btnStandard");
		btnOK.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				session.courseVersion(courseVersion.getUUID()).getUploadURL(new Callback<String>() {
					@Override
					public void ok(String url) {
						getFile(url);
					}
				});		
			}
		});
		courseVersionUpload.add(btnOK);
	}

	@Override
	public void init(CourseVersion courseVersion, Wizard wizard) {
		this.courseVersion = courseVersion;
		isWizardVersion = (wizard != null);
		wizardContainer.clear();
		if(isWizardVersion){
			this.wizard = wizard;
			this.selectedWizardElement = presenter.getSelectedWizardElement();
			wizardView = new WizardView(session, bus);
			wizardView.init(courseVersion, wizard, presenter);
			wizardContainer.add(wizardView);
		}
		courseVersionUpload.setVisible(!isWizardVersion);
	}
	
	public static native void getFile(String url) /*-{
		if ($wnd.document.getElementById("versionUpdate").files.length != 1) {
        	@kornell.gui.client.util.view.KornellNotification::showError(Ljava/lang/String;)("Por favor selecione um arquivo");
		} else {
			@kornell.gui.client.util.view.LoadingPopup::show()();
			var file = $wnd.document.getElementById("versionUpdate").files[0];
			if (file.name.indexOf(".zip") == -1) {
	        	@kornell.gui.client.util.view.KornellNotification::showError(Ljava/lang/String;)("Faça o upload de um arquivo zip");
				@kornell.gui.client.util.view.LoadingPopup::hide()();
			} else {
				var req = new XMLHttpRequest();
				req.open('PUT', url);
				req.setRequestHeader("Content-type", "application/zip");
				req.onreadystatechange = function() {
    				if (req.readyState == 4 && req.status == 200) {
        				@kornell.gui.client.util.view.LoadingPopup::hide()();
        				@kornell.gui.client.util.view.KornellNotification::show(Ljava/lang/String;)("Atualização de versão completa");
    				}
				}
				req.send(file);
			}
		}
	}-*/;

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public WizardView getWizardView() {
		return wizardView;
	}

}