package kornell.gui.client.presentation.admin.courseversion.courseversion.generic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.github.gwtbootstrap.client.ui.FileUpload;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
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
import kornell.core.entity.InstitutionType;
import kornell.core.to.ChatThreadMessageTO;
import kornell.core.to.UnreadChatThreadTO;
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionView;
import kornell.gui.client.event.UnreadMessagesCountChangedEvent;
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionContentView;
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionContentView.Presenter;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.Wizard;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardElement;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardFactory;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardMock;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlide;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlideItem;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlideItemType;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardTopic;
import kornell.gui.client.presentation.message.MessagePanelType;
import kornell.gui.client.presentation.message.generic.MessageItem;
import kornell.gui.client.util.forms.formfield.KornellFormFieldWrapper;

public class GenericAdminCourseVersionContentView extends Composite implements AdminCourseVersionContentView {
	interface MyUiBinder extends UiBinder<Widget, GenericAdminCourseVersionContentView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private EventBus bus;
	private KornellSession session;
	boolean isCurrentUser, showContactDetails, isRegisteredWithCPF;
	private List<Label> sideItems;
	private HashMap<String, Label> sidePanelItemsMap;
	private static String HIGHLIGHT_CLASS = "highlightText";
	private String PLAIN_CLASS = "plainDiscreteTextColor";
	private WizardElement currentWizardElement;
	private List<KornellFormFieldWrapper> fields;

	@UiField
	FlowPanel courseVersionUpload;

	@UiField
	FlowPanel wizardPanel;
	@UiField	
	FlowPanel sidePanel;
	@UiField	
	FlowPanel slidePanel;
	@UiField
	Label slideTitle;
	@UiField
	ScrollPanel slideItemsScroll;
	@UiField
	FlowPanel slidePanelItems;

	private CourseVersion courseVersion;
	private Presenter presenter;
	
	private boolean isWizardVersion = false;

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
	public void init(CourseVersion courseVersion) {
		this.courseVersion = courseVersion;
		isWizardVersion = ContentSpec.WIZARD.equals(courseVersion.getContentSpec());
		
		wizardPanel.setVisible(false);
		
		if(isWizardVersion){			
			Wizard wizard = WizardMock.mockWizard();
			updateSidePanel(wizard, wizard.getWizardTopics().get(0).getWizardSlides().get(0));
		}
		courseVersionUpload.setVisible(!isWizardVersion);
		
		wizardPanel.setVisible(true);
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
	public Presenter getPresenter() {
		return presenter;
	}

	@Override
	public void displaySlidePanel(boolean display) {
		slidePanel.setVisible(display);
	}

	@Override
	public void updateSlidePanel(Wizard wizard, WizardElement wizardElement) {
		//threadTitle.getElement().setInnerHTML(getThreadTitle(unreadChatThreadTO, currentUserFullName, false));
		sidePanelItemsMap = new HashMap<String, Label>();
		slidePanelItems.clear();

		/*name = new KornellFormFieldWrapper("Nome", formHelper.createTextBoxFormField(courseVersion.getName()), isInstitutionAdmin);
		fields.add(name);
		courseVersionFields.add(name);*/

		if(wizardElement instanceof WizardSlide){
			WizardSlide wizardSlide = (WizardSlide) wizardElement;
			for (final WizardSlideItem wizardSlideItem : wizardSlide.getWizardSlideItems()) {
				FlowPanel threadMessageWrapper = new FlowPanel();
				threadMessageWrapper.addStyleName("threadMessageWrapper");
				Label header = new Label("");

				header.addStyleName("threadMessageHeader");
				threadMessageWrapper.add(header);

				Label item = new Label(wizardSlideItem.getTitle());
				item.addStyleName("threadMessageItem");
				threadMessageWrapper.add(item);
				slidePanelItems.add(threadMessageWrapper);
			} 
		}
	}
	
	/*private boolean validateFields() {		
		if (!formHelper.isLengthValid(name.getFieldPersistText(), 2, 100)) {
			name.setError("Insira o título");
		}
		
		return !formHelper.checkErrors(fields);
	}*/

	@Override
	public void updateSidePanel(Wizard wizard, WizardElement selectedWizardElement) {
		currentWizardElement = selectedWizardElement;
		sidePanel.clear();
		sideItems = new ArrayList<Label>();
		sidePanelItemsMap = new HashMap<String, Label>();
		for (final WizardTopic wizardTopic : wizard.getWizardTopics()) {
			createSidePanelItem(wizard, selectedWizardElement, null, wizardTopic);
			for (final WizardSlide wizardSlide : wizardTopic.getWizardSlides()) {
				createSidePanelItem(wizard, selectedWizardElement, wizardTopic, wizardSlide);				
			}
		}
	}

	private void createSidePanelItem(Wizard wizard, WizardElement selectedWizardElement, final WizardTopic parentWizardElement,
			WizardElement wizardElement) {
		final Label label = new Label();
		label.addStyleName("sidePanelItem");
		label.addClickHandler(new ClickHandler() {
			boolean enableClick = true;
			@Override
			public void onClick(ClickEvent event) {
				if(!enableClick) return;
				enableClick = false;
				Timer preventDoubleClickTimer = new Timer() {
					public void run() {
						enableClick = true;
					}
				};
				preventDoubleClickTimer.schedule(300);

				for (Label lbl : sideItems) {
					lbl.removeStyleName("selected");
				}
				label.addStyleName("selected");
				presenter.wizardElementClicked(wizard, wizardElement);
				setLabelContent(parentWizardElement, wizardElement, label);
				
				currentWizardElement = selectedWizardElement;
			}
		});
		if(currentWizardElement.getUUID().equals(selectedWizardElement.getUUID())){
			label.addStyleName("selected");
		}
		if(parentWizardElement != null){
			label.addStyleName("marginLeft25");
		}
		setLabelContent(parentWizardElement, wizardElement, label);
		
		sidePanel.add(label);
		sideItems.add(label);
	}

	private void setLabelContent(WizardElement parentWizardElement, WizardElement wizardElement, final Label label) {
		String type = parentWizardElement == null ? "Tópico " : ("Slide " + parentWizardElement.getOrder() + "."); 
		String title = span(type + wizardElement.getOrder(), HIGHLIGHT_CLASS) + separator(true) + span(wizardElement.getTitle(), PLAIN_CLASS);
		String titleStripped = title.replaceAll(separator(true), " ").replaceAll("\\<[^>]*>","").replaceAll(separator(false, true), " ").replaceAll(separator(false, false), " ").toLowerCase();
		sidePanelItemsMap.put(titleStripped, label);
		label.getElement().setInnerHTML(title);
	}

	private String separator(boolean lineBreak) {
		return separator(lineBreak, false);
	}

	private String separator(boolean lineBreak, boolean dash) {
		return lineBreak ? "<br>" : (dash ? "&nbsp;&nbsp;-&nbsp;&nbsp;" : "&nbsp;&nbsp;&nbsp;");
	}

	private String span(String str, String className) {
		return "<span class=\""+className+"\">"+str+"</span>";
	}
}